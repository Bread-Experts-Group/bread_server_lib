package org.bread_experts_group.http

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.http.h2.*
import org.bread_experts_group.http.h2.setting.HTTP2SettingEnableServerPush
import org.bread_experts_group.http.h2.setting.HTTP2SettingInitialWindowSize
import org.bread_experts_group.http.h2.setting.HTTP2SettingMaxConcurrentStreams
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.ConsolidatedBlockingInputStream
import org.bread_experts_group.stream.readString
import org.bread_experts_group.stream.scanDelimiter
import org.bread_experts_group.stream.writeString
import java.io.InputStream
import java.io.OutputStream
import java.net.URI
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.logging.Logger

@OptIn(ExperimentalStdlibApi::class)
class HTTPProtocolSelector(
	val version: HTTPVersion,
	val from: InputStream?,
	val to: OutputStream,
	val server: Boolean
) {
	companion object {
		const val HTTP2_PREFACE: String = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
	}

	val logger: Logger = ColoredHandler.newLogger(LoggerResource.get().getString("http_selector") + " ($version)")
	val requestBacklog: LinkedBlockingQueue<Result<HTTPRequest>> = LinkedBlockingQueue<Result<HTTPRequest>>()
	val responseBacklog: LinkedBlockingQueue<Result<HTTPResponse>> = LinkedBlockingQueue<Result<HTTPResponse>>()

	private fun versionInitialization(from: InputStream) = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			fun decodeHeaders(): Map<String, String> = buildMap {
				while (true) {
					val read = from.scanDelimiter("\r\n").split(':', limit = 2)
					if (read.size != 2) break
					set(read[0].lowercase(), if (read[1][0] == ' ') read[1].substring(1) else read[1])
				}
			}

			fun decodeData(headers: Map<String, String>): ConsolidatedBlockingInputStream {
				val data = ConsolidatedBlockingInputStream()
				if (headers.contains("transfer-encoding")) {
					if (headers.getValue("transfer-encoding") != "chunked")
						throw UnsupportedOperationException("TE: ${headers.getValue("transfer-encoding")}")
					while (true) {
						val length = from.scanDelimiter("\r\n").toInt(16)
						if (length == 0) break
						data.streams.add(from.readNBytes(length).inputStream())
						from.scanDelimiter("\r\n")
					}
				} else if (headers.contains("content-length")) data.streams.add(
					from.readNBytes(headers.getValue("content-length").toInt()).inputStream()
				)
				return data
			}

			Thread.ofVirtual().name("HTTP/0.9-1.1 Backlogger").start {
				while (true) {
					if (server) {
						val method = HTTPMethod.safeMapping[from.scanDelimiter(" ")] ?: HTTPMethod.OTHER
						val path = URI(
							from
								.scanDelimiter(" ")
								.replace(Regex("%(?![0-9a-fA-F]{2})"), "%25")
						)
						from.scanDelimiter("\r\n").let {
							val version = HTTPVersion.mapping[it]
							when (version) {
								null -> throw DecodingException("Client sent a bad HTTP version [$it]")
								!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
									throw DecodingException("Client sent unsupported HTTP version [$version]")
								}

								else -> version
							}
						}
						val headers = decodeHeaders()
						requestBacklog.add(
							Result.success(
								HTTPRequest(
									method,
									path,
									headers,
									decodeData(headers)
								)
							)
						)
					} else {
						from.scanDelimiter(" ").let {
							val version = HTTPVersion.mapping[it]
							when (version) {
								null -> throw DecodingException("Server sent a bad HTTP version [$it]")
								!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
									throw DecodingException("Server sent unsupported HTTP version [$version]")
								}

								else -> version
							}
						}
						val code = from.scanDelimiter("\r\n").let {
							val parsed = it.split(' ', limit = 2)[0].toIntOrNull()
							if (parsed == null) throw DecodingException("Server sent bad status code [$it]")
							parsed
						}
						val headers = decodeHeaders()
						responseBacklog.add(
							Result.success(
								HTTPResponse(
									HTTPRequest(HTTPMethod.GET, URI.create("/")),
									code,
									headers,
									decodeData(headers),
									true
								)
							)
						)
					}
				}
			}.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, t ->
				if (server) requestBacklog.add(Result.failure(t))
				else responseBacklog.add(Result.failure(t))
			}
		}

		HTTPVersion.HTTP_2 -> {
			if (server) {
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
			}
			Thread.ofVirtual().name("HTTP/2 Backlogger").start {
				val streams = mutableMapOf<Int, ConsolidatedBlockingInputStream>()
				val headerBlocks = mutableMapOf<Int, MutableMap<String, String>>()
				var dynamic = listOf<Pair<String, String>>()

				val backlog = ArrayDeque<HTTP2Frame>()
				var clientSettingsOK = false
				var serverSettingsOK = false
				while (true) {
					val frame = (if (clientSettingsOK && serverSettingsOK) backlog.poll() else null)
						?: HTTP2Frame.read(from, dynamic)
					logger.fine("> $frame")
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
								val stream = streams.getOrPut(frame.identifier) { ConsolidatedBlockingInputStream() }
								requestBacklog.add(
									Result.success(
										HTTP2Request(
											frame.identifier,
											HTTPMethod.safeMapping[blocks.remove(":method")] ?: HTTPMethod.OTHER,
											URI(
												blocks.remove(":scheme"),
												blocks.remove(":authority"),
												blocks.remove(":path"),
												null,
												null
											),
											blocks.toMap(),
											stream
										)
									)
								)
								blocks.clear()
							}
						}

						is HTTP2DataFrame -> {
							val stream = streams.getOrPut(frame.identifier) { ConsolidatedBlockingInputStream() }
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
			}.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, t ->
				if (server) requestBacklog.add(Result.failure(t))
				else responseBacklog.add(Result.failure(t))
			}
		}

		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
	}

	init {
		if (from != null) versionInitialization(from)
	}

	fun nextRequest(): Result<HTTPRequest> = requestBacklog.take()

	fun sendRequest(request: HTTPRequest): Long = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			to.writeString("${request.method.name} ${request.path} ${version.tag}\r\n")
			request.headers.forEach { (key, value) -> to.writeString("$key:$value\r\n") }
			to.writeString("\r\n")
			request.data.transferTo(to)
		}

		HTTPVersion.HTTP_2 -> TODO("HTTP/2")
		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
	}

	fun nextResponse(): Result<HTTPResponse> = responseBacklog.take()

	fun sendResponse(response: HTTPResponse): Long? = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			to.writeString("${version.tag} ${response.code}\r\n")
			response.headers.forEach { (key, value) -> to.writeString("$key:$value\r\n") }
			to.writeString("\r\n")
			response.data.transferTo(to)
		}

		HTTPVersion.HTTP_2 -> {
			val request = response.to
			if (request !is HTTP2Request)
				throw UnsupportedOperationException("HTTP/2 response must be to HTTP/2 request")
			HTTP2HeaderFrame(
				request.stream,
				buildList {
					add(HTTP2HeaderFrameFlag.END_OF_HEADERS)
					if (response.data.available() == 0) add(HTTP2HeaderFrameFlag.END_OF_STREAM)
				},
				null,
				mapOf(
					":status" to response.code.toString()
				) + response.headers
			)
				.also { logger.fine("< $it") }
				.write(to)
			if (response.data.available() > 0) {
				// TODO: Factor for window/frame limits
				HTTP2DataFrame(request.stream, listOf(HTTP2DataFrameFlag.END_OF_STREAM), response.data.readAllBytes())
					.also { logger.fine("< $it") }
					.write(to)
			}
			null
		}

		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
	}
}