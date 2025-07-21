package org.bread_experts_group.protocol.http

import org.bread_experts_group.channel.CRLFi
import org.bread_experts_group.channel.ReadingByteBuffer
import org.bread_experts_group.channel.SPi
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.numeric.coercedInt
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.CRLF
import java.net.URI
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.nio.channels.WritableByteChannel
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore
import java.util.logging.Logger

@OptIn(ExperimentalStdlibApi::class)
class HTTPProtocolSelector(
	val version: HTTPVersion,
	val from: ReadableByteChannel?,
	val to: WritableByteChannel,
	val server: Boolean
) {
	companion object {
		const val HTTP2_PREFACE: String = "PRI * HTTP/2.0\r\n\r\nSM\r\n\r\n"
	}

	val logger: Logger = ColoredHandler.newLogger(LoggerResource.get().getString("http_selector") + " ($version)")
	val transmissionLog: ArrayDeque<HTTPRequest> = ArrayDeque()
	val requestBacklog: LinkedBlockingQueue<Result<HTTPRequest>> = LinkedBlockingQueue()
	val responseBacklog: LinkedBlockingQueue<Result<HTTPResponse>> = LinkedBlockingQueue()

	private fun versionInitialization(reading: ReadingByteBuffer) = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			val fromLock = Semaphore(1)
			fun decodeHeaders(): Map<String, String> = buildMap {
				while (true) {
					val read = reading.decodeString(Charsets.ISO_8859_1, CRLFi).split(':', limit = 2)
					if (read.size != 2) break
					set(read[0].lowercase(), if (read[1][0] == ' ') read[1].substring(1) else read[1])
				}
			}

			fun decodeData(headers: Map<String, String>): ReadableByteChannel {
				return object : ReadableByteChannel {
					private var locked = false
					private var transferEncodingState =
						if (headers.containsKey("transfer-encoding")) TransferEncodingState.INITIAL_CHUNK
						else TransferEncodingState.NO_TRANSFER_ENCODING
					private var remaining = if (transferEncodingState == TransferEncodingState.NO_TRANSFER_ENCODING)
						headers["content-length"]?.toULong() ?: 0uL
					else 0uL

					init {
						if (
							transferEncodingState != TransferEncodingState.NO_TRANSFER_ENCODING ||
							remaining > 0uL
						) {
							locked = true
							fromLock.acquire()
						}
					}

					override fun read(dst: ByteBuffer): Int {
						if (!locked) return -1
						if (transferEncodingState != TransferEncodingState.NO_TRANSFER_ENCODING) {
							if (remaining == 0uL) {
								if (transferEncodingState == TransferEncodingState.REST_OF_DATA)
									reading.decodeString(Charsets.US_ASCII, CRLFi)
								remaining = reading.decodeString(Charsets.US_ASCII, CRLFi).toULong(16)
								if (remaining == 0uL) {
									reading.decodeString(Charsets.US_ASCII, CRLFi)
									close()
									return -1
								}
								transferEncodingState = TransferEncodingState.REST_OF_DATA
								return read(dst)
							} else {
								val transfer = reading.transferTo(dst, remaining.coercedInt)
								remaining -= transfer.toULong()
								return transfer
							}
						}
						if (remaining == 0uL) {
							close()
							return -1
						}
						val read = reading.transferTo(dst, remaining.coercedInt)
						remaining -= read.toULong()
						return read
					}

					override fun isOpen(): Boolean = locked
					override fun close() {
						fromLock.release()
						locked = false
					}
				}
			}

			Thread.ofVirtual().name("HTTP/0.9-1.1 Backlogger").start {
				while (true) {
					fromLock.acquire()
					if (server) {
						val method = HTTPMethod.entries.id(reading.decodeString(Charsets.US_ASCII, SPi))
						val path = URI(
							reading
								.decodeString(Charsets.US_ASCII, SPi)
								.replace(Regex("%(?![0-9a-fA-F]{2})"), "%25")
						)
						reading.decodeString(Charsets.US_ASCII, CRLFi).let {
							val version = HTTPVersion.entries.id(it)
							when (version) {
								!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
									throw InvalidInputException("Client sent unsupported HTTP version [$it]")
								}

								else -> version
							}
						}
						val headers = decodeHeaders()
						fromLock.release()
						requestBacklog.add(
							Result.success(
								HTTPRequest(
									method,
									path,
									headers,
									decodeData(headers),
									true
								)
							)
						)
					} else {
						reading.decodeString(Charsets.US_ASCII, SPi).let {
							val version = HTTPVersion.entries.id(it)
							when (version) {
								!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
									throw InvalidInputException("Server sent unsupported HTTP version [$it]")
								}

								else -> version
							}
						}
						val code = reading.decodeString(Charsets.ISO_8859_1, CRLFi).let {
							val parsed = it.split(' ', limit = 2)[0].toIntOrNull()
							if (parsed == null) throw InvalidInputException("Server sent bad status code [$it]")
							parsed
						}
						val headers = decodeHeaders()
						fromLock.release()
						responseBacklog.add(
							Result.success(
								HTTPResponse(
									transmissionLog.pollFirst() ?: HTTPRequest(HTTPMethod.GET, URI.create("/")),
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

//		HTTPVersion.HTTP_2 -> {
//			if (server) {
//				val readPreface = input.readString(HTTP2_PREFACE.length)
//				if (readPreface != HTTP2_PREFACE) throw InvalidInputException(
//					"Client sent bad HTTP/2 preface;\n" +
//							"Expected: 0x" + HTTP2_PREFACE.toByteArray().joinToString("") {
//						it.toString(16).padStart(2, '0').uppercase()
//					} +
//							"\nRead    : 0x" + readPreface.toByteArray().joinToString("") {
//						it.toString(16).padStart(2, '0').uppercase()
//					}
//				)
//			}
//			Thread.ofVirtual().name("HTTP/2 Backlogger").start {
//				val streams = mutableMapOf<Int, ConsolidatedInputStream>()
//				val headerBlocks = mutableMapOf<Int, MutableMap<String, String>>()
//				var dynamic = listOf<Pair<String, String>>()
//
//				val backlog = ArrayDeque<HTTP2Frame>()
//				var clientSettingsOK = false
//				var serverSettingsOK = false
//				while (true) {
//					val frame = (if (clientSettingsOK && serverSettingsOK) backlog.poll() else null)
//						?: HTTP2Frame.read(input, dynamic)
//					logger.fine("> $frame")
//					if (clientSettingsOK && !serverSettingsOK && frame !is HTTP2SettingsFrame) {
//						backlog.add(frame)
//						continue
//					}
//					when (frame) {
//						is HTTP2SettingsFrame -> {
//							TODO("HTTP/2 Settings")
//							if (!frame.flags.contains(HTTP2SettingsFrameFlag.ACKNOWLEDGED)) {
//								if (clientSettingsOK) throw _root_ide_package_.kotlin.IllegalStateException(
//									"Already parsed HTTP/2 settings"
//								)
//								// TODO: Parse settings in accordance with frame
//								clientSettingsOK = true
//								HTTP2SettingsFrame(
//									listOf(),
//									listOf(
//										HTTP2SettingEnableServerPush(false),
//										HTTP2SettingInitialWindowSize(65536),
//										HTTP2SettingMaxConcurrentStreams(100)
//									)
//								).also { logger.fine("< $it") }//.write(to)
//								logger.fine("Client settings ACKed")
//							} else {
//								logger.fine("Server settings ACKed")
//								serverSettingsOK = true
//							}
//						}
//
//						is HTTP2WindowUpdateFrame -> {} // TODO window
//
//						is HTTP2HeaderFrame -> {
//							dynamic = frame.dynamic
//							val blocks = headerBlocks.getOrPut(frame.identifier) { mutableMapOf() }
//							blocks += frame.block
//							if (frame.flags.contains(HTTP2HeaderFrameFlag.END_OF_HEADERS)) {
//								val stream = streams.getOrPut(frame.identifier) { ConsolidatedInputStream(true) }
//								requestBacklog.add(
//									Result.success(
//										HTTP2Request(
//											frame.identifier,
//											HTTPMethod.entries.id(blocks.remove(":method") ?: "GET"),
//											URI(
//												blocks.remove(":scheme"),
//												blocks.remove(":authority"),
//												blocks.remove(":path"),
//												null,
//												null
//											),
//											blocks.toMap(),
//											stream
//										)
//									)
//								)
//								blocks.clear()
//							}
//						}
//
//						is HTTP2DataFrame -> {
//							TODO("HTTP/2 data")
//							val stream = streams.getOrPut(frame.identifier) { ConsolidatedInputStream(true) }
//							stream.streams += frame.data.inputStream()
//							HTTP2WindowUpdateFrame(frame.identifier, frame.data.size)
//								.also { logger.fine("< $it") }
////								.write(to)
//							HTTP2WindowUpdateFrame(0, frame.data.size)
//								.also { logger.fine("< $it") }
////								.write(to)
//						}
//
//						is HTTP2ShutdownFrame -> {
//							input.close()
//							to.close()
//							break
//						}
//
//						is HTTP2StopStreamFrame -> {} // TODO Stream states
//					}
//				}
//			}.uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, t ->
//				if (server) requestBacklog.add(Result.failure(t))
//				else responseBacklog.add(Result.failure(t))
//			}
//		}

		HTTPVersion.HTTP_2 -> TODO("未来")
		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
		HTTPVersion.OTHER -> throw IllegalArgumentException("Invalid version")
	}

	init {
		if (from != null) versionInitialization(
			ReadingByteBuffer(
				from,
				ByteBuffer.allocateDirect(8192),
				null
			)
		)
	}

	fun nextRequest(): Result<HTTPRequest> = requestBacklog.take()

	private val sendBuffer = ByteBuffer.allocateDirect(8192)
	private val chunkedBuffer = ByteBuffer.allocateDirect(18)
	private fun txData11(headline: ByteArray, data: ReadableByteChannel, size: Long?): Long {
		sendBuffer.clear()
		sendBuffer.put(headline)
		var sent = 0L
		if (size != null) {
			if (data is FileChannel) {
				sendBuffer.flip()
				to.write(sendBuffer)
				while (sent < size) {
					sent += data.transferTo(sent, size - sent, to)
				}
			} else {
				do {
					val read = data.read(sendBuffer)
					sent += read
					sendBuffer.flip()
					to.write(sendBuffer)
					sendBuffer.clear()
				} while (read != -1)
				sent++
			}
		} else {
			do {
				val read = data.read(sendBuffer)
				sent += read
				chunkedBuffer.clear()
				for (c in Integer.toHexString(read)) chunkedBuffer.put(c.code.toByte())
				chunkedBuffer.put(CRLF)
				chunkedBuffer.flip()
				to.write(chunkedBuffer)
				sendBuffer.flip()
				to.write(sendBuffer)
				chunkedBuffer.limit(2)
				chunkedBuffer.rewind()
				chunkedBuffer.put(CRLF)
				chunkedBuffer.flip()
				to.write(chunkedBuffer)
				sendBuffer.clear()
			} while (read != -1)
			chunkedBuffer.limit(5)
			chunkedBuffer.rewind()
			chunkedBuffer.put(48) // '0'
			chunkedBuffer.put(CRLF)
			chunkedBuffer.put(CRLF)
			chunkedBuffer.rewind()
			to.write(chunkedBuffer)
			sent++
		}
		return sent
	}

	private fun StringBuilder.appendLengthHeaders(headers: Map<String, String>, size: Long?) {
		if (headers.containsKey("content-length") || headers.containsKey("transfer-encoding")) return
		if (size != null) {
			append("content-length:")
			append(size)
		} else {
			append("transfer-encoding:")
			append("chunked")
		}
		append("\r\n")
	}

	fun sendRequest(request: HTTPRequest): Long = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			transmissionLog.add(request)
			val size =
				if (request.data is SeekableByteChannel) request.data.size()
				else request.headers["content-length"]?.toLongOrNull()
			val headline = buildString {
				append(request.method.name)
				append(' ')
				append(request.path)
				append(' ')
				append(version.tag)
				append("\r\n")
				request.headers.forEach { (key, value) ->
					append(key)
					append(':')
					append(value)
					append("\r\n")
				}
				appendLengthHeaders(request.headers, size)
				append("\r\n")
			}.toByteArray()
			txData11(headline, request.data, size)
		}

		HTTPVersion.HTTP_2 -> TODO("HTTP/2")
		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
		HTTPVersion.OTHER -> throw IllegalArgumentException("Invalid version")
	}

	fun nextResponse(): Result<HTTPResponse> = responseBacklog.take()

	fun sendResponse(response: HTTPResponse): Long? = when (version) {
		HTTPVersion.HTTP_0_9, HTTPVersion.HTTP_1_0, HTTPVersion.HTTP_1_1 -> {
			val size =
				if (response.data is SeekableByteChannel) response.data.size()
				else response.headers["content-length"]?.toLongOrNull()
			val headline = buildString {
				append(version.tag)
				append(' ')
				append(response.code)
				append("\r\n")
				response.headers.forEach { (key, value) ->
					append(key)
					append(':')
					append(value)
					append("\r\n")
				}
				appendLengthHeaders(response.headers, size)
				append("\r\n")
			}.toByteArray()
			txData11(headline, response.data, size)
		}

//		HTTPVersion.HTTP_2 -> {
//			val request = response.to
//			if (request !is HTTP2Request)
//				throw UnsupportedOperationException("HTTP/2 response must be to HTTP/2 request")
//			HTTP2HeaderFrame(
//				request.stream,
//				buildList {
//					add(HTTP2HeaderFrameFlag.END_OF_HEADERS)
//					if (response.data.size() == 0L) add(HTTP2HeaderFrameFlag.END_OF_STREAM)
//				},
//				null,
//				mapOf(
//					":status" to response.code.toString()
//				) + response.headers
//			)
//				.also { logger.fine("< $it") }
//				.write(to)
//			if (response.data.size() > 0) {
//				// TODO: Factor for window/frame limits
//				HTTP2DataFrame(request.stream, listOf(HTTP2DataFrameFlag.END_OF_STREAM), response.data.readAllBytes())
//					.also { logger.fine("< $it") }
//					.write(to)
//			}
//			null
//		}

		HTTPVersion.HTTP_2 -> TODO("HTTP/2")
		HTTPVersion.HTTP_3 -> TODO("HTTP/3")
		HTTPVersion.OTHER -> throw IllegalArgumentException("Invalid version")
	}
}