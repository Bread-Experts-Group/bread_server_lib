package org.bread_experts_group.protocol.old.http.provider

import org.bread_experts_group.channel.CRLFi
import org.bread_experts_group.channel.SPi
import org.bread_experts_group.channel.WritingByteBuffer
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.io.reader.ReadingByteBuffer
import org.bread_experts_group.numeric.coercedInt
import org.bread_experts_group.protocol.old.http.*
import java.net.URI
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SeekableByteChannel
import java.util.*
import java.util.concurrent.Semaphore

class HTTP11Provider : HTTPProvider(HTTPVersion.HTTP_1_1) {
	fun decodeHeaders(from: ReadingByteBuffer): Map<String, String> = buildMap {
		while (true) {
			val read = from.decodeString(Charsets.ISO_8859_1, CRLFi).split(':', limit = 2)
			if (read.size != 2) break
			set(read[0].lowercase(), if (read[1][0] == ' ') read[1].substring(1) else read[1])
		}
	}

	fun decodeData(fromLock: Semaphore, from: ReadingByteBuffer, headers: Map<String, String>): ReadableByteChannel {
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
							from.decodeString(Charsets.US_ASCII, CRLFi)
						remaining = from.decodeString(Charsets.US_ASCII, CRLFi).toULong(16)
						if (remaining == 0uL) {
							from.decodeString(Charsets.US_ASCII, CRLFi)
							close()
							return -1
						}
						transferEncodingState = TransferEncodingState.REST_OF_DATA
						return read(dst)
					} else {
						val transfer = from.transferTo(dst, remaining.coercedInt)
						remaining -= transfer.toULong()
						return transfer
					}
				}
				if (remaining == 0uL) {
					close()
					return -1
				}
				val read = from.transferTo(dst, remaining.coercedInt)
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

	override fun setupClientRead(
		from: ReadingByteBuffer,
		transmissionLog: Queue<HTTPRequest>,
		backlog: Queue<Result<HTTPResponse>>
	) {
		val fromLock = Semaphore(1)
		while (true) {
			from.decodeString(Charsets.US_ASCII, SPi).let {
				val version = HTTPVersion.entries.id(it).enum!!
				when (version) {
					!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
						throw InvalidInputException("Server sent unsupported HTTP version [$it]")
					}

					else -> version
				}
			}
			val code = from.decodeString(Charsets.ISO_8859_1, CRLFi).let {
				val parsed = it.split(' ', limit = 2)[0].toIntOrNull()
				if (parsed == null) throw InvalidInputException("Server sent bad status code [$it]")
				parsed
			}
			val headers = decodeHeaders(from)
			fromLock.release()
			backlog.add(
				Result.success(
					HTTPResponse(
						transmissionLog.remove(),
						code,
						headers,
						decodeData(fromLock, from, headers),
						true
					)
				)
			)
		}
	}

	override fun setupServerRead(from: ReadingByteBuffer, backlog: Queue<Result<HTTPRequest>>) {
		val fromLock = Semaphore(1)
		while (true) {
			fromLock.acquire()
			val method = HTTPMethod.entries.id(from.decodeString(Charsets.US_ASCII, SPi)).enum!!
			val path = URI(
				from
					.decodeString(Charsets.US_ASCII, SPi)
					.replace(Regex("%(?![0-9a-fA-F]{2})"), "%25")
			)
			from.decodeString(Charsets.US_ASCII, CRLFi).let {
				val version = HTTPVersion.entries.id(it).enum!!
				when (version) {
					!in HTTPVersion.HTTP_0_9..HTTPVersion.HTTP_1_1 -> {
						throw InvalidInputException("Client sent unsupported HTTP version [$it]")
					}

					else -> version
				}
			}
			val headers = decodeHeaders(from)
			fromLock.release()
			backlog.add(
				Result.success(
					HTTPRequest(
						method,
						path,
						headers,
						decodeData(fromLock, from, headers),
						true
					)
				)
			)
		}
	}

	private fun sendData(to: WritingByteBuffer, headline: ByteArray, data: ReadableByteChannel, size: Long?): Long {
		to.put(headline)
		var sent = 0L
		if (size != null) {
			if (data is FileChannel) {
				to.flush()
				while (sent < size) {
					sent += data.transferTo(sent, size - sent, to.to)
				}
			} else {
				to.transferFrom(data)
			}
		} else {
			TODO("Delta")
		}
		to.flush()
		return sent
	}

	private fun appendLengthHeaders(headers: Map<String, String>, size: Long?): String {
		if (headers.containsKey("content-length") || headers.containsKey("transfer-encoding")) return ""
		return if (size != null) "content-length:$size\r\n"
		else "transfer-encoding:chunked\r\n"
	}

	override fun sendRequest(request: HTTPRequest, transmissionLog: Queue<HTTPRequest>, to: WritingByteBuffer) {
		transmissionLog.add(request)
		val size =
			if (request.data is SeekableByteChannel) request.data.size()
			else request.headers["content-length"]?.toLongOrNull()
		var headline = "${request.method.name} ${request.path} ${version.tag}\r\n"
		request.headers.forEach { (key, value) -> headline += "$key:$value\r\n" }
		headline += appendLengthHeaders(request.headers, size) + "\r\n"
		sendData(to, headline.toByteArray(Charsets.ISO_8859_1), request.data!!, size)
	}

	override fun sendResponse(response: HTTPResponse, to: WritingByteBuffer) {
		val size =
			if (response.data is SeekableByteChannel) response.data.size()
			else response.headers["content-length"]?.toLongOrNull()
		var headline = "${version.tag} ${response.code}\r\n"
		response.headers.forEach { (key, value) -> headline += "$key:$value\r\n" }
		headline += appendLengthHeaders(response.headers, size) + "\r\n"
		sendData(to, headline.toByteArray(), response.data!!, size)
	}
}