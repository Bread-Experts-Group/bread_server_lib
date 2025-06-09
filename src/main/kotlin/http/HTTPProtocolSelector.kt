package org.bread_experts_group.http

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.http.h2.*
import org.bread_experts_group.http.h2.setting.HTTP2SettingEnableServerPush
import org.bread_experts_group.http.h2.setting.HTTP2SettingInitialWindowSize
import org.bread_experts_group.http.h2.setting.HTTP2SettingMaxConcurrentStreams
import org.bread_experts_group.logging.ColoredLogger
import org.bread_experts_group.stream.ConsolidatedInputStream
import org.bread_experts_group.stream.readString
import org.bread_experts_group.stream.scanDelimiter
import org.bread_experts_group.stream.writeString
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@OptIn(ExperimentalStdlibApi::class)
class HTTPProtocolSelector(
	val version: HTTPVersion,
	val from: InputStream,
	val to: OutputStream
) {
	companion object {
		const val HTTP2_PREFACE: String = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
	}

	val logger = ColoredLogger.newLogger("HTTP Protocol Selector ($version)")
	val requestBacklog = LinkedBlockingQueue<HTTPRequest>()

	init {
		when (version) {
			HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
				Thread.ofVirtual().name("HTTP/0.9-1.1 Request Backlogger").start {
					while (true) {
						val method = HTTPMethod.safeMapping[from.scanDelimiter(" ")] ?: HTTPMethod.OTHER
						val url = URI(from.scanDelimiter(" ").replace(Regex("%(?![0-9a-fA-F]{2})"), "%25"))
						val version = from.scanDelimiter("\r\n").let {
							val version = HTTPVersion.mapping[it]
							when (version) {
								null -> throw DecodingException("Client sent a bad HTTP version [$it]")
								!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
									throw DecodingException("Client sent unsupported HTTP version [$version]")
								}

								else -> version
							}
						}
						val headers = buildMap {
							while (true) {
								val raw = from.scanDelimiter("\r\n")
								if (raw.isEmpty()) break
								var (name, value) = raw.split(':', ignoreCase = true, limit = 2)
								if (value[0] == ' ') value = value.substring(1)
								name = name
									.lowercase()
									.split('-')
									.joinToString("-") { it.replaceFirstChar { ch -> ch.uppercaseChar() } }
								this[name] = value
							}
						}
						requestBacklog.add(HTTPRequest(method, url, version, headers, byteArrayOf().inputStream()))
					}
				}
			}

			HTTPVersion.HTTP_2 -> {
				val readPreface = from.readString(HTTP2_PREFACE.length)
				if (readPreface != HTTP2_PREFACE) throw DecodingException(
					"Client sent bad HTTP/2 preface;\n" +
							"Expected: 0x" + HTTP2_PREFACE.toByteArray().joinToString("") {
						it.toString(16).padStart(2, '0').uppercase()
					} +
							"\nRead    : 0x" + readPreface.toByteArray().joinToString("") {
						it.toString(16).padStart(2, '0').uppercase()
					}
				)
				Thread.ofVirtual().name("HTTP/2 Request Backlogger").start {
					val streams = mutableMapOf<Int, ConsolidatedInputStream>()
					val headerBlocks = mutableMapOf<Int, MutableMap<String, String>>()
					var dynamic = listOf<Pair<String, String>>()

					var backlog = ArrayDeque<HTTP2Frame>()
					var clientSettingsOK = false
					var serverSettingsOK = false
					while (true) {
						val frame = (if (clientSettingsOK && serverSettingsOK) backlog.poll() else null)
							?: HTTP2Frame.read(from, dynamic)
						if (clientSettingsOK && !serverSettingsOK && frame !is HTTP2SettingsFrame) {
							backlog.add(frame)
							continue
						}
						when (frame) {
							is HTTP2SettingsFrame -> {
								if (!frame.flags.contains(HTTP2SettingsFrameFlag.ACKNOWLEDGED)) {
									if (clientSettingsOK) throw DecodingException("Already parsed HTTP/2 settings")
									// TODO: Parse settings in accordance with frame
									clientSettingsOK = true
									HTTP2SettingsFrame(
										listOf(),
										listOf(
											HTTP2SettingEnableServerPush(false),
											HTTP2SettingInitialWindowSize(65536),
											HTTP2SettingMaxConcurrentStreams(100)
										)
									).also { logger.fine("< $it") }.write(to)
									logger.fine("Client settings ACKed")
								} else {
									logger.fine("Server settings ACKed")
									serverSettingsOK = true
								}
							}

							is HTTP2WindowUpdateFrame -> {} // TODO window

							is HTTP2HeaderFrame -> {
								dynamic = frame.dynamic
								val blocks = headerBlocks.getOrPut(frame.identifier) { mutableMapOf() }
								blocks += frame.block
								if (frame.flags.contains(HTTP2HeaderFrameFlag.END_OF_HEADERS)) {
									val stream = streams.getOrPut(frame.identifier) { ConsolidatedInputStream() }
									requestBacklog.add(
										HTTPRequest(
											HTTPMethod.safeMapping[blocks.remove(":method")] ?: HTTPMethod.OTHER,
											URI(
												blocks.remove(":scheme"),
												blocks.remove(":authority"),
												blocks.remove(":path"),
												null,
												null
											),
											HTTPVersion.HTTP_2,
											blocks,
											stream
										)
									)
									blocks.clear()
								}
							}

							is HTTP2DataFrame -> {
								val stream = streams.getOrPut(frame.identifier) { ConsolidatedInputStream() }
								stream.streams += frame.data.inputStream()
								HTTP2WindowUpdateFrame(frame.identifier, frame.data.size)
									.also { logger.fine("< $it") }
									.write(to)
								HTTP2WindowUpdateFrame(0, frame.data.size)
									.also { logger.fine("< $it") }
									.write(to)
							}

							is HTTP2ShutdownFrame -> {
								from.close()
								to.close()
								break
							}

							is HTTP2StopStreamFrame -> {} // TODO Stream states
						}
					}
				}
			}

			HTTPVersion.HTTP_3 -> TODO("HTTP/3")
		}
	}

	fun nextRequest(): HTTPRequest = requestBacklog.take()

	fun sendResponse(response: HTTPResponse) = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			to.writeString("${version.tag} ${response.code}\r\n")
			response.headers.forEach { (key, value) -> to.writeString("$key:$value\r\n") }
			to.writeString("\r\n")
		}

		HTTPVersion.HTTP_2 -> {
			if (response.data.available() > 0) TODO("Data length")
			HTTP2HeaderFrame(
				1,
				listOf(HTTP2HeaderFrameFlag.END_OF_HEADERS, HTTP2HeaderFrameFlag.END_OF_STREAM),
				null,
				mapOf(
					":status" to response.code.toString()
				) + response.headers
			)
				.also { logger.fine("< $it") }
				.write(to)
		}

		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
	}
}