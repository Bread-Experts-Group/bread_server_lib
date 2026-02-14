package org.bread_experts_group.generic.protocol.http.h2

import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.generic.io.reader.BSLReader
import org.bread_experts_group.generic.io.reader.BSLWriter
import org.bread_experts_group.generic.logging.LevelLogger
import org.bread_experts_group.generic.logging.LogMessage
import org.bread_experts_group.generic.protocol.http.h2.HTTP2ConnectionManager.Companion.create
import org.bread_experts_group.generic.protocol.huffman.HuffmanBranch
import org.bread_experts_group.generic.protocol.huffman.HuffmanCut
import org.bread_experts_group.generic.protocol.huffman.HuffmanEdge
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.Semaphore
import kotlin.math.min
import kotlin.random.Random
import kotlin.system.exitProcess

/**
 * Managing class for HTTP/2 connections.
 * @param reader Internal constructor: [BSLReader] used for incoming data.
 * @param writer Internal constructor: [BSLWriter] used for outgoing data.
 * @see create
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
class HTTP2ConnectionManager private constructor(
	private val reader: BSLReader<*, *>,
	private val writer: BSLWriter<*, *>
) : HTTP2ConnectionManagerCreationData {
	val logger = LevelLogger<LogMessage>("http2")
	private var stageSetMaxHeaderTableSize: Int = 4096
	private var stageSetMaxFrameSize: Int = 16384
	private var stageSetMaxConcurrentStreams: Int = 100
	private var stageSetInitialWindowSize = 65535

	private var localSetMaxHeaderTableSize: Int = stageSetMaxHeaderTableSize
	private var localSetMaxFrameSize: Int = stageSetMaxFrameSize
	private var localSetMaxConcurrentStreams: Int = Int.MAX_VALUE
	private var localSetInitialWindowSize = stageSetInitialWindowSize

	private var peerMaxHeaderTableSize: Int = stageSetMaxHeaderTableSize
	private var peerMaxFrameSize: Int = stageSetMaxFrameSize
	private var peerEnablePush: Boolean = false
	private var peerSetMaxConcurrentStreams: Int = Int.MAX_VALUE
	private var peerInitialWindowSize = 65535

	private val closedStreams = mutableSetOf<Int>()
	private val connectionStreams =
		mutableMapOf<Int, HTTP2ConnectionState>()
	private var connectionState =
		HTTP2ConnectionState(65535)
	private var peerHighestStreamID = 0

	private var peerLastConsumedStreamID: Int? = null

	private fun readFrame(): HTTP2ReadFrameData {
		val i4 = reader.readS32()
		val length = (i4 and 0xFFFFFF00.toInt()) ushr 8
		if (length > localSetMaxFrameSize) {
			reader.skip(6)
			return HTTP2ReadFrameStatus.TooLarge(
				length
			)
		}
		val frameType =
			HTTP2StandardFrameTypes.entries.id(i4 and 0xFF)
		val flags = reader.readU8i()
		val streamIdentifier = reader.readU32l() and 0b01111111_11111111_11111111_11111111
		return HTTP2Frame(
			length,
			frameType,
			flags,
			streamIdentifier.toInt()
		)
	}

	private val writeLock = Semaphore(1)
	private fun signalGoAway(
		code: MappedEnumeration<UInt, HTTP2StandardErrorCodes>,
		debugData: ByteArray
	) {
		writeLock.acquire()
		val dataSize = min(debugData.size, localSetMaxFrameSize - 8)
		writer.write32(0x07 or ((dataSize + 8) shl 8))
		writer.write8(0)
		writer.write32(0)
		writer.write32(peerLastConsumedStreamID ?: 0)
		writer.write32(code.raw)
		writer.write(debugData, length = dataSize)
		writer.flush()
		writeLock.release()
	}

	private fun interpretSettingsFrame(frame: HTTP2Frame): Boolean {
		if (frame.flags and 0x01 != 0) {
			if (frame.length != 0) {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_SETTINGS_ACK_SIZE_ERROR)
				signalGoAway(
					MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR),
					byteArrayOf()
				)
				return false
			}
			localSetMaxConcurrentStreams = stageSetMaxConcurrentStreams
			localSetMaxHeaderTableSize = stageSetMaxHeaderTableSize
			localSetMaxFrameSize = stageSetMaxFrameSize
			return true
		}
		if (frame.streamIdentifier != 0) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_SETTINGS_IDENTIFIER_ERROR)
			signalGoAway(
				MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR),
				byteArrayOf()
			)
			return false
		}
		var lengthRemaining = frame.length
		while (lengthRemaining >= 6) {
			val identifier =
				HTTP2StandardSettings.entries.id(
					reader.readU16i()
				)
			val value = reader.readS32()
			when (identifier.enum) {
				HTTP2StandardSettings.SETTINGS_HEADER_TABLE_SIZE -> {
					peerMaxHeaderTableSize = value
				}

				HTTP2StandardSettings.SETTINGS_ENABLE_PUSH -> peerEnablePush =
					when (value) {
						0 -> false
						1 -> true
						else -> {
							this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_SETTINGS_ENABLE_PUSH_NOT_BOOLEAN_ERROR)
							signalGoAway(
								MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR),
								byteArrayOf()
							)
							return false
						}
					}

				HTTP2StandardSettings.SETTINGS_MAX_CONCURRENT_STREAMS -> {
					peerSetMaxConcurrentStreams = value
				}

				HTTP2StandardSettings.SETTINGS_INITIAL_WINDOW_SIZE -> {
					if (value < 0) {
						this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_SETTINGS_WINDOW_SIZE_NEGATIVE)
						signalGoAway(
							MappedEnumeration(HTTP2StandardErrorCodes.FLOW_CONTROL_ERROR),
							byteArrayOf()
						)
						return false
					}
					val difference = value - peerInitialWindowSize
					connectionStreams.forEach { (_, stream) ->
						stream.flowControl.addAndGet(difference)
						stream.flowControlSignal.release()
					}
					peerInitialWindowSize = value
				}

				HTTP2StandardSettings.SETTINGS_MAX_FRAME_SIZE -> {
					if (value !in 16384..16777215) {
						this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_SETTINGS_MAX_FRAME_SIZE_OUT_OF_RANGE)
						signalGoAway(
							MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR),
							byteArrayOf()
						)
						return false
					}
					peerMaxFrameSize = value
				}

				null -> println("Unknown ID: $identifier , $value")
			}
			lengthRemaining -= 6
		}
		if (lengthRemaining > 0) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_SETTINGS_SIZE_ERROR)
			signalGoAway(
				MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR),
				byteArrayOf()
			)
			return false
		}
		writeLock.acquire()
		writer.write32(0x04)
		writer.write8i(0b0000000_1)
		writer.write32(0)
		writer.flush()
		writeLock.release()
		return true
	}

	private fun writeSettingsFrame() {
		var delta = 0
		val headerSizeChanged = if (stageSetMaxHeaderTableSize != localSetMaxHeaderTableSize) {
			delta++
			true
		} else false
		val maxStreamsChanged = if (stageSetMaxConcurrentStreams != localSetMaxConcurrentStreams) {
			delta++
			true
		} else false
		val windowSizeChanged = if (stageSetInitialWindowSize != localSetInitialWindowSize) {
			delta++
			true
		} else false
		val frameSizeChanged = if (stageSetMaxFrameSize != localSetMaxFrameSize) {
			delta++
			true
		} else false
		writeLock.acquire()
		writer.write32(0x04 or ((delta * 6) shl 8))
		writer.write8(0)
		writer.write32(0)
		if (headerSizeChanged) {
			writer.write16i(HTTP2StandardSettings.SETTINGS_HEADER_TABLE_SIZE.id)
			writer.write32(stageSetMaxHeaderTableSize)
		}
		if (maxStreamsChanged) {
			writer.write16i(HTTP2StandardSettings.SETTINGS_MAX_CONCURRENT_STREAMS.id)
			writer.write32(stageSetMaxConcurrentStreams)
		}
		if (windowSizeChanged) {
			writer.write16i(HTTP2StandardSettings.SETTINGS_INITIAL_WINDOW_SIZE.id)
			writer.write32(stageSetInitialWindowSize)
		}
		if (frameSizeChanged) {
			writer.write16i(HTTP2StandardSettings.SETTINGS_MAX_FRAME_SIZE.id)
			writer.write32(stageSetMaxFrameSize)
		}
		writer.flush()
		writeLock.release()
	}

	fun signalStreamReset(
		stream: Int,
		code: MappedEnumeration<UInt, HTTP2StandardErrorCodes>
	) {
		writeLock.acquire()
		writer.write32(0x03 or (4 shl 8))
		writer.write8(0)
		writer.write32(stream)
		writer.write32(code.raw)
		writer.flush()
		writeLock.release()
		closedStreams.add(stream)
		connectionStreams.remove(stream)
	}

	private fun interpretWindowUpdateFrame(frame: HTTP2Frame): Boolean {
		if (frame.length != 4) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_WINDOW_UPDATE_SIZE_ERROR)
			signalGoAway(
				MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR),
				byteArrayOf()
			)
			return false
		}
		val state = if (frame.streamIdentifier == 0) connectionState else connectionStreams[frame.streamIdentifier]
		if (state == null) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_WINDOW_UPDATE_IDENTIFIER_ERROR)
			signalGoAway(
				MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR),
				byteArrayOf()
			)
			return false
		}
		val increment = reader.readS32()
		if (increment == 0) {
			if (frame.streamIdentifier == 0) {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_WINDOW_UPDATE_INCREMENT_0_ERROR)
				signalGoAway(
					MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR),
					byteArrayOf()
				)
				return false
			} else {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_WINDOW_UPDATE_INCREMENT_0_ERROR)
				signalStreamReset(
					frame.streamIdentifier,
					MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
				)
				return true
			}
		}
		val checkValue = state.flowControl.get()
		if (checkValue + increment < checkValue) {
			if (frame.streamIdentifier == 0) {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_WINDOW_UPDATE_INCREMENT_OVERFLOW_ERROR)
				signalGoAway(
					MappedEnumeration(HTTP2StandardErrorCodes.FLOW_CONTROL_ERROR),
					byteArrayOf()
				)
				return false
			} else {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_WINDOW_UPDATE_INCREMENT_OVERFLOW_ERROR)
				signalStreamReset(
					frame.streamIdentifier,
					MappedEnumeration(HTTP2StandardErrorCodes.FLOW_CONTROL_ERROR)
				)
				return true
			}
		}
		state.flowControl.addAndGet(increment)
		state.flowControlSignal.release()
		return true
	}

	private val dataLock = Semaphore(1)
	private val streamHeaders =
		LinkedBlockingQueue<HTTP2HeaderMessage>()

	fun nextStreamHeaders(): HTTP2HeaderMessage {
		val pair = streamHeaders.take()
		peerLastConsumedStreamID = pair.stream
		return pair
	}

	fun startGetStreamData(stream: Int): Int {
		val stream = connectionStreams[stream] ?: TODO("??")
		stream.dataSignal.acquire()
		return stream.nextDataLength
	}

	fun endGetStreamData(stream: Int) {
		val stream = connectionStreams[stream] ?: TODO("??")
		reader.skip(stream.padSkip.toLong())
		dataLock.release()
	}

	fun sendHeaders(
		streamID: Int, headers: Map<String, String>,
		endData: Boolean, tryPad: Boolean = false
	) {
		val stream = connectionStreams[streamID] ?: TODO("??")
		if (endData) {
			if (stream.state == HTTP2ConnectionState.State.CLOSED_REMOTE) {
				connectionStreams.remove(streamID)
				closedStreams.add(streamID)
			} else stream.state = HTTP2ConnectionState.State.CLOSED_LOCAL
		}

		// TODO: Huffman, Dynamic Table
		val headerData = ByteArrayOutputStream()
		fun writeInteger(integer: Int, prefix: Int, suffixLength: Int) {
			var o0mask = 0
			repeat(suffixLength) { o0mask = o0mask or (1 shl it) }
			headerData.write((prefix shl suffixLength) or (integer and o0mask))
			var remainder = integer ushr suffixLength
			while (remainder > 0) {
				val shifted = remainder ushr 7
				headerData.write((remainder and 0b0_1111111) or (if (shifted > 0) 0b1_0000000 else 0))
				remainder = shifted
			}
		}

		fun writeString(key: String) {
			writeInteger(key.length, 0, 7)
			headerData.write(key.toByteArray(Charsets.ISO_8859_1))
		}

		fun writeHeader(key: String, value: String) {
			headerData.write(0b0001_0000)
			writeString(key)
			writeString(value)
		}

		headers.forEach { (key, value) -> writeHeader(key, value) }

		val data = headerData.toByteArray()
		var toSend = minOf(0xFFFFFF - (if (tryPad) 256 else 0), peerMaxFrameSize, data.size)
		var flags = if (toSend == data.size) 0b00000100 else 0
		if (endData) flags = flags or 0b00000001
		val padLength = if (tryPad && toSend < peerMaxFrameSize) {
			val padLength = Random.nextInt(0, min((peerMaxFrameSize - toSend - 1), 255))
			flags = flags or 0b00001000
			padLength
		} else -1
		writeLock.acquire()
		writer.write32(0x01 or ((toSend + (if (padLength >= 0) (padLength + 1) else 0)) shl 8))
		writer.write8i(flags)
		writer.write32(streamID)
		if (padLength >= 0) writer.write8i(padLength)
		writer.write(data, length = toSend)
		if (padLength >= 0) writer.fill(padLength.toLong())

		var offset = toSend
		while (offset < data.size) {
			toSend = minOf(0xFFFFFF, peerMaxFrameSize, data.size - offset)
			writer.write32(0x09 or (toSend shl 8))
			writer.write8(if ((toSend + offset) == data.size) 0b00000100 else 0)
			writer.write32(streamID)
			writer.write(data, offset, toSend)
			offset += toSend
		}
		writer.flush()
		writeLock.release()
	}

	private var padLength: Int = 0
	fun sendData(
		streamID: Int,
		data: ByteArray,
		endData: Boolean,
		tryPad: Boolean = false
	) {
		val stream = connectionStreams[streamID] ?: TODO("??")
		var offset = 0
		while (offset < data.size) {
			var flowControlN: Int = stream.flowControl.get()
			while (flowControlN < 1) {
				stream.flowControlSignal.acquire()
				flowControlN = stream.flowControl.get()
				if (flowControlN > 0) break
			}
			val toSend = minOf(
				0xFFFFFF - (if (tryPad) 256 else 0),
				peerMaxFrameSize,
				data.size - offset,
				flowControlN
			)
			writeLock.acquire()
			writer.write32(0x00 or (toSend shl 8))
			writer.write8(
				if ((toSend + offset == data.size) && endData) 0b00000001 else 0
			) // TODO pad
			writer.write32(streamID)
			writer.write(data, offset, toSend)
			writer.flush()
			writeLock.release()
			offset += toSend
			stream.flowControl.addAndGet(-toSend)
		}
		if (endData) {
			if (stream.state == HTTP2ConnectionState.State.CLOSED_REMOTE) {
				connectionStreams.remove(streamID)
				closedStreams.add(streamID)
			} else stream.state = HTTP2ConnectionState.State.CLOSED_LOCAL
		}
	}

	private val dynamicTable = ArrayDeque<Pair<String, String>>()
	private var hPackMaxTableSize = localSetMaxHeaderTableSize
	private var dynamicTableSimSize = 0
	private fun updateHPACKTableSize() {
		while (dynamicTableSimSize > hPackMaxTableSize) {
			val (removeKey, removeValue) = dynamicTable.removeLast()
			dynamicTableSimSize -= removeKey.length + removeValue.length + 32
		}
	}

	private fun addToDynamicTable(key: String, value: String) {
		val simSize = key.length + value.length + 32
		if (simSize > hPackMaxTableSize) return
		dynamicTableSimSize += simSize
		updateHPACKTableSize()
		dynamicTable.addFirst(key to value)
	}

	private fun interpretHeadersFrame(frame: HTTP2Frame): Boolean {
		if (closedStreams.contains(frame.streamIdentifier)) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_CLOSED_IDENTIFIER)
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.STREAM_CLOSED), byteArrayOf())
			return false
		}
		if (frame.streamIdentifier % 2 != 1 || frame.streamIdentifier < peerHighestStreamID) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_IDENTIFIER_ERROR)
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		val stream = connectionStreams[frame.streamIdentifier]
		if (stream?.state == HTTP2ConnectionState.State.CLOSED_REMOTE) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_CLOSED_REMOTE)
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.STREAM_CLOSED)
			)
			reader.skip(frame.length.toLong())
			return true
		}
		if (stream == null && connectionStreams.size + 1 == localSetMaxConcurrentStreams) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_TOO_MANY_STREAMS)
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.REFUSED_STREAM)
			)
			reader.skip(frame.length.toLong())
			return true
		}
		peerHighestStreamID = frame.streamIdentifier
		val priority = (frame.flags and 0b00100000) != 0
		val padded = (frame.flags and 0b00001000) != 0
		var endHeaders = (frame.flags and 0b00000100) != 0
		val endStream = (frame.flags and 0b00000001) != 0
		val padLength = if (padded) reader.readU8i() else -1
		if (padLength >= frame.length) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_PAD_ERROR)
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (priority) {
			reader.readS32() // deprecated exclusive/stream dependency
			reader.readS8() // deprecated weight
		}
		fun decodeHuffman(data: ByteArray): String {
			val constructed = StringBuilder(data.size * 2)
			var bitOffset = 0
			var byteOffset = 0
			fun nextBit(): Int {
				if (byteOffset == data.size) return -1
				val read = data[byteOffset].toInt() and (1 shl (7 - bitOffset++))
				if (bitOffset == 8) {
					bitOffset = 0
					byteOffset++
				}
				return if (read != 0) 1 else 0
			}

			var position = huffmanCode
			var huffman = ""
			while (true) {
				val nextBit = nextBit()
				if (nextBit == -1) break
				val node = when (nextBit) {
					0 -> position.zero.also { huffman += "0" }
					1 -> position.one.also { huffman += "1" }
					else -> throw IllegalStateException()
				}
				when (node) {
					is HuffmanBranch<Char> -> position = node
					is HuffmanEdge<Char> -> {
						huffman = ""
						constructed.append(node.value)
						position = huffmanCode
					}

					is HuffmanCut<Char> -> {
						println("cut ... $huffman")
						exitProcess(1)
					}
				}
			}
			return constructed.toString()
		}

		val fields = mutableMapOf<String, MutableList<String>>()
		var dataLength = frame.length - ((if (priority) 5 else 0) + (if (padLength >= 0) padLength + 1 else 0))
		fun getContinuation(): Boolean {
			val cFrame = readFrame() as? HTTP2Frame ?: TODO("Handle nonframe")
			if (cFrame.type.enum != HTTP2StandardFrameTypes.CONTINUATION) {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_NON_CONTINUATION_ERROR)
				signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
				return false
			}
			if (cFrame.streamIdentifier != frame.streamIdentifier) {
				this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_CONTINUATION_IDENTIFIER_ERROR)
				signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
				return false
			}
			endHeaders = cFrame.flags and 0b00000100 != 0
			dataLength = cFrame.length
			return true
		}

		fun readNextByte(): Int? {
			if (dataLength == 0) {
				if (endHeaders) {
					this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_OUT_OF_DATA)
					signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
					return null
				} else if (!getContinuation()) return null
			}
			dataLength--
			return reader.readU8i()
		}

		fun readNextBytes(n: Int): ByteArray? {
			val bytes = ByteArray(n)
			var offset = 0
			while (offset < n) {
				if (dataLength == 0) {
					if (endHeaders) {
						this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_OUT_OF_DATA)
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return null
					} else if (!getContinuation()) return null
				}
				val toRead = min(dataLength, n - offset)
				reader.read(bytes, length = toRead)
				offset += toRead
				dataLength -= toRead
			}
			return bytes
		}

		fun decodeInteger(octet: Int, prefixLength: Int): Int? {
			var bitOffset = 0
			var mask = 0
			while (bitOffset < prefixLength) mask = mask or (1 shl bitOffset++)
			var prefix = octet and mask
			if (prefix == mask) {
				var m = 0
				do {
					val b = readNextByte() ?: return null
					prefix += ((b and 0b0_1111111) shl m)
					m += 7
				} while (b and 0b1_0000000 != 0)
			}
			return prefix
		}

		fun decodeString(): String? {
			val o = readNextByte() ?: return null
			val size = decodeInteger(o, 7) ?: return null
			val data = readNextBytes(size) ?: return null
			return if (o and 0b1_0000000 != 0) decodeHuffman(data) else data.toString(Charsets.ISO_8859_1)
		}

		// TODO: dynamicTable [index - staticTable.size]
		println("---")
		while (!(dataLength == 0 && endHeaders)) {
			val o0 = readNextByte() ?: return false
			when {
				o0 and 0b1_0000000 != 0 -> {
					val index = decodeInteger(o0, 7)
					if (index == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					val kv = staticTable[index] ?: dynamicTable.getOrNull(index - staticTable.size)
					if (kv == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					if (kv.first == ":authority") println("GAMMA ${kv.first}, ${kv.second}, ${fields[kv.first]}")
					fields.getOrPut(kv.first) { mutableListOf() }.add(kv.second)
				}

				o0 and 0b11_000000 == 0b01_000000 -> {
					val index = decodeInteger(o0, 6)
					if (index == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					val key = if (index == 0) {
						decodeString()
					} else {
						(staticTable[index] ?: dynamicTable.getOrNull(index - staticTable.size))?.first
					}
					if (key == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					val value = decodeString()
					if (value == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					addToDynamicTable(key, value)
					if (key == ":authority") println("BETA $key, $value, ${fields[key]}")
					fields.getOrPut(key) { mutableListOf() }.add(value)
				}

				o0 and 0b1111_0000 == 0 || o0 and 0b1111_0000 == 0b1_0000 -> {
					val index = decodeInteger(o0, 4)
					if (index == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					val key = if (index == 0) {
						decodeString()
					} else {
						(staticTable[index] ?: dynamicTable.getOrNull(index - staticTable.size))?.first
					}
					if (key == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					val value = decodeString()
					if (value == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					if (key == ":authority") println("ALPHA $key, $value, ${fields[key]}")
					fields.getOrPut(key) { mutableListOf() }.add(value)
				}

				o0 and 0b111_00000 == 0b001_00000 -> {
					val size = decodeInteger(o0, 5)
					if (size == null) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					if (size > localSetMaxHeaderTableSize) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.COMPRESSION_ERROR), byteArrayOf())
						return false
					}
					hPackMaxTableSize = size
					updateHPACKTableSize()
				}

				else -> TODO("? ${o0.toString(2).padStart(8, '0')}")
			}
		}
		if (
			fields.containsKey("connection") ||
			fields.containsKey("proxy-connection") ||
			fields.containsKey("keep-alive") ||
			fields.containsKey("transfer-encoding") ||
			fields.containsKey("upgrade")
		) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_CONNECTION_HEADERS)
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
			)
			return true
		}
		val te = fields["te"]
		if (te != null && te.all { it == "trailers" }) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_HEADERS_TE_NOT_TRAILERS)
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
			)
			return true
		}
		/*
		TODO: When a request message violates one of these requirements, an implementation SHOULD generate a 400
			(Bad Request) status code (see Section 15.5.1 of [HTTP]), unless a more suitable status code is
			defined or the status code cannot be sent (e.g., because the error occurs in a trailer field).
		 */
		val authority = fields[":authority"]
		if (authority != null) {
			val host = fields["Host"]
			if ((host != null && authority.first() != host.first()) || authority.size > 1) {
				signalStreamReset(
					frame.streamIdentifier,
					MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
				)
				return true
			}
		}
		val method = fields[":method"]
		if (method == null || method.size > 1) {
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
			)
			return true
		}
		if (method.first() != "CONNECT") {
			val path = fields[":path"]
			val scheme = fields[":scheme"]
			if (
				path == null ||
				path.size > 1 ||
				path.first().isEmpty() ||
				scheme == null ||
				scheme.size > 1
			) {
				signalStreamReset(
					frame.streamIdentifier,
					MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
				)
				return true
			}
		} else {
			if (fields.containsKey(":scheme") || fields.containsKey(":path")) {
				signalStreamReset(
					frame.streamIdentifier,
					MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
				)
				return true
			}
		}
		for ((fieldName, fieldValues) in fields) {
			val noColonAllowed = !(fieldName == ":method" || fieldName == ":scheme" || fieldName == ":authority" ||
					fieldName == ":path")
			for (char in fieldName) {
				val code = char.code
				if (code in 0x00..0x20 || code in 0x41..0x5A || code in 0x7F..0xFF ||
					(noColonAllowed && code == 0x3A)
				) {
					signalStreamReset(
						frame.streamIdentifier,
						MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
					)
					return true
				}
			}
			fieldValues.forEach { fieldValue ->
				var i = 0
				for (char in fieldValue) {
					val code = char.code
					if (code == 0x00 || code == 0x0A || code == 0x0D ||
						((code == 0x20 || code == 0x09) && (i == 0 || i == fieldValue.lastIndex))
					) {
						signalStreamReset(
							frame.streamIdentifier,
							MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR)
						)
						return true
					}
					i++
				}
			}
		}
		val newState = HTTP2ConnectionState(peerInitialWindowSize)
		if (endStream) {
			newState.state = HTTP2ConnectionState.State.CLOSED_REMOTE
			newState.dataSignal.release()
		}
		connectionStreams[frame.streamIdentifier] = newState
		if (padLength > 0) reader.skip(padLength.toLong())
		streamHeaders.add(
			HTTP2HeaderMessage(
				fields,
				endStream,
				frame.streamIdentifier
			)
		)
		return true
	}

	private fun interpretDataFrame(frame: HTTP2Frame): Boolean {
		if (frame.streamIdentifier == 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (closedStreams.contains(frame.streamIdentifier)) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.STREAM_CLOSED), byteArrayOf())
			return false
		}
		val stream = connectionStreams[frame.streamIdentifier]
		if (stream == null) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (stream.state == HTTP2ConnectionState.State.CLOSED_REMOTE) {
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.STREAM_CLOSED)
			)
			reader.skip(frame.length.toLong())
			return true
		}
		var dataLength = frame.length
		if (frame.flags and 0b00001000 != 0) {
			val padLength = reader.readU8i()
			if (padLength >= dataLength) {
				signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
				return false
			}
			stream.padSkip = padLength
			dataLength -= stream.padSkip + 1
		}
		if (frame.flags and 0b00000001 == 1) {
			if (stream.state == HTTP2ConnectionState.State.CLOSED_LOCAL)
				connectionStreams.remove(frame.streamIdentifier)
			else stream.state = HTTP2ConnectionState.State.CLOSED_REMOTE
		}
		stream.nextDataLength = dataLength
		stream.dataSignal.release()
		return true
	}

	private fun interpretPriorityFrame(frame: HTTP2Frame): Boolean {
		if (frame.streamIdentifier == 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (frame.length != 5) {
			signalStreamReset(
				frame.streamIdentifier,
				MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR)
			)
			reader.skip(frame.length.toLong())
			return true
		}
		reader.skip(5)
		return true
	}

	private fun interpretPingFrame(frame: HTTP2Frame): Boolean {
		if (frame.streamIdentifier != 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (frame.length != 8) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR), byteArrayOf())
			return false
		}
		if (frame.flags and 0b00000001 == 0) {
			writeLock.acquire()
			writer.write32(0x0806)
			writer.write8(0b00000001)
			writer.write32(0)
			writer.write64(reader.readU64k())
			writer.flush()
			writeLock.release()
		} else reader.skip(8)
		return true
	}

	private fun interpretStreamResetFrame(frame: HTTP2Frame): Boolean {
		if (frame.streamIdentifier == 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (!connectionStreams.containsKey(frame.streamIdentifier)) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (frame.length != 4) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR), byteArrayOf())
			return false
		}
		closedStreams.add(frame.streamIdentifier)
		connectionStreams.remove(frame.streamIdentifier)
		reader.skip(4) // TODO FIGURE OUT WHy!!!!
		return true
	}

	private fun interpretGoAwayFrame(frame: HTTP2Frame): Boolean {
		if (frame.streamIdentifier != 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		reader.skip(frame.length.toLong()) // TODO GO AWAY
		return true
	}

	private fun managingThread() {
		val initialFrame = readFrame()
		if (initialFrame !is HTTP2Frame || initialFrame.type.enum != HTTP2StandardFrameTypes.SETTINGS) {
			this.logger.log(HTTP2LogIdentifiers.HTTP2_FAILURE_PREFACE_NO_SETTINGS_FRAME)
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return
		}
		writeSettingsFrame()
		if (!interpretSettingsFrame(initialFrame)) return
		while (true) {
			dataLock.acquire()
			when (val frame = readFrame()) {
				is HTTP2Frame -> when (frame.type.enum) {
					HTTP2StandardFrameTypes.WINDOW_UPDATE -> {
						if (!interpretWindowUpdateFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.HEADERS -> {
						if (!interpretHeadersFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.SETTINGS -> {
						if (!interpretSettingsFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.PRIORITY -> {
						if (!interpretPriorityFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.PING -> {
						if (!interpretPingFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.RST_STREAM -> {
						if (!interpretStreamResetFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.GOAWAY -> {
						if (!interpretGoAwayFrame(frame)) return
						dataLock.release()
					}

					HTTP2StandardFrameTypes.CONTINUATION -> {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
						return
					}

					HTTP2StandardFrameTypes.DATA -> if (!interpretDataFrame(frame)) return

					else -> {
						println(frame)
						reader.skip(frame.length.toLong())
						dataLock.release()
					}
				}

				is HTTP2ReadFrameStatus.TooLarge -> {
					signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR), byteArrayOf())
					return
				}
			}
		}
	}

	init {
		Thread.ofVirtual().start(::managingThread)
	}

	companion object {
		/**
		 * Creates an [HTTP2ConnectionManager] from an associated reader/writer for communication with a client.
		 * Before the manager is created, the clients preface is partially checked; `PRI * HTTP/2.0` must have been
		 * consumed before calling, but not `\r\n\r\nSM\r\n\r\n`.
		 * @param reader The associated reader (client -> this).
		 * @param writer The associated writer (this -> client).
		 * @author Miko Elbrecht
		 * @since D1F3N6P0
		 */
		fun create(
			reader: BSLReader<*, *>,
			writer: BSLWriter<*, *>
		): HTTP2ConnectionManagerCreationData {
			if (
				!reader.readN(10).contentEquals(
					byteArrayOf(
						'\r'.code.toByte(),
						'\n'.code.toByte(),
						'\r'.code.toByte(),
						'\n'.code.toByte(),
						'S'.code.toByte(),
						'M'.code.toByte(),
						'\r'.code.toByte(),
						'\n'.code.toByte(),
						'\r'.code.toByte(),
						'\n'.code.toByte(),
					)
				)
			) return HTTP2ClientPrefaceIncorrect
			reader.order = ByteOrder.BIG_ENDIAN
			writer.order = ByteOrder.BIG_ENDIAN
			return HTTP2ConnectionManager(reader, writer)
		}
	}
}