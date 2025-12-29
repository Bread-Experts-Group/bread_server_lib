package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.io.reader.BSLReader
import org.bread_experts_group.io.reader.BSLWriter
import org.bread_experts_group.protocol.http.h2.HTTP2ConnectionManager.Companion.create
import org.bread_experts_group.protocol.huffman.HuffmanBranch
import org.bread_experts_group.protocol.huffman.HuffmanCut
import org.bread_experts_group.protocol.huffman.HuffmanEdge
import java.io.ByteArrayOutputStream
import java.nio.ByteOrder
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.min

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

	private val connectionStreams = mutableMapOf<Int, HTTP2ConnectionState>()
	private var connectionState = HTTP2ConnectionState(65535)
	private var localLastStreamID: Int = 0
	private var peerLastStreamID: Int? = null

	private fun readFrame(): HTTP2ReadFrameData {
		val i4 = reader.readS32()
		val length = (i4 and 0xFFFFFF00.toInt()) ushr 8
		if (length > localSetMaxFrameSize) {
			reader.skip(6)
			return HTTP2ReadFrameStatus.TooLarge(length)
		}
		val frameType = HTTP2StandardFrameTypes.entries.id(i4 and 0xFF)
		val flags = reader.readU8i()
		val streamIdentifier = reader.readU32l() and 0b01111111_11111111_11111111_11111111
		return HTTP2Frame(length, frameType, flags, streamIdentifier.toInt())
	}

	private fun signalGoAway(
		code: MappedEnumeration<UInt, HTTP2StandardErrorCodes>,
		debugData: ByteArray
	) {
		val dataSize = min(debugData.size, localSetMaxFrameSize - 8)
		writer.write32(0x07 or ((dataSize + 8) shl 8))
		writer.write8(0)
		writer.write32(0)
		writer.write32(peerLastStreamID ?: 0)
		writer.write32(code.raw)
		writer.write(debugData, length = dataSize)
		writer.flush()
	}

	private fun interpretSettingsFrame(frame: HTTP2Frame): Boolean {
		if (frame.flags and 0x01 != 0) {
			if (frame.length != 0) {
				signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR), byteArrayOf())
				return false
			}
			localSetMaxHeaderTableSize = stageSetMaxHeaderTableSize
			localSetMaxFrameSize = stageSetMaxFrameSize
			return true
		}
		if (frame.streamIdentifier != 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		var lengthRemaining = frame.length
		while (lengthRemaining >= 6) {
			val identifier = HTTP2StandardSettings.entries.id(reader.readU16i())
			val value = reader.readS32()
			when (identifier.enum) {
				HTTP2StandardSettings.SETTINGS_HEADER_TABLE_SIZE -> {
					peerMaxHeaderTableSize = value
				}

				HTTP2StandardSettings.SETTINGS_ENABLE_PUSH -> peerEnablePush = when (value) {
					0 -> false
					1 -> true
					else -> {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
						return false
					}
				}

				HTTP2StandardSettings.SETTINGS_MAX_CONCURRENT_STREAMS -> {
					peerSetMaxConcurrentStreams = value
				}

				HTTP2StandardSettings.SETTINGS_INITIAL_WINDOW_SIZE -> {
					peerInitialWindowSize = value
				}

				HTTP2StandardSettings.SETTINGS_MAX_FRAME_SIZE -> {
					if (value !in 16384..16777215) {
						signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
						return false
					}
					peerMaxFrameSize = value
				}

				null -> println("Unknown ID: $identifier , $value")
			}
			lengthRemaining -= 6
		}
		if (lengthRemaining > 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR), byteArrayOf())
			return false
		}
		writer.write32(0x04)
		writer.write8i(0b0000000_1)
		writer.write32(0)
		writer.flush()
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
	}

	private fun interpretWindowUpdate(frame: HTTP2Frame): Boolean {
		if (frame.length != 4) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FRAME_SIZE_ERROR), byteArrayOf())
			return false
		}
		val increment = reader.readS32()
		if (increment == 0) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		val state = if (frame.streamIdentifier == 0) connectionState else connectionStreams[frame.streamIdentifier]
		if (state == null) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (state.flowControl + increment < state.flowControl) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.FLOW_CONTROL_ERROR), byteArrayOf())
			return false
		}
		state.flowControl += increment
		return true
	}

	private val streamHeaders = LinkedBlockingQueue<Pair<Int, Map<String, String>>>()
	fun nextStreamHeaders(): Pair<Int, Map<String, String>> {
		val pair = streamHeaders.take()
		peerLastStreamID = pair.first
		return pair
	}

	fun sendHeaders(stream: Int, headers: Map<String, String>) {
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
		writer.write32(0x01 or (headerData.size() shl 8))
		writer.write8(0b00000100)
		writer.write32(stream)
		writer.write(headerData.toByteArray())
		writer.flush()
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
		if (frame.streamIdentifier != (peerLastStreamID ?: -1) + 2) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		val priority = (frame.flags and 0b00100000) != 0
		val padded = (frame.flags and 0b00001000) != 0
		val endHeaders = (frame.flags and 0b00000100) != 0
		val endStream = (frame.flags and 0b00000001) != 0
		val padLength = if (padded) reader.readU8i() else 0
		if (padLength >= frame.length) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return false
		}
		if (priority) {
			reader.readS32() // deprecated exclusive/stream dependency
			reader.readS8() // deprecated weight
		}
		if (!endHeaders) TODO("end h")
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
			while (true) {
				val nextBit = nextBit()
				if (nextBit == -1) break
				val node = when (nextBit) {
					0 -> position.zero
					1 -> position.one
					else -> throw IllegalStateException()
				}
				when (node) {
					is HuffmanBranch<Char> -> position = node
					is HuffmanEdge<Char> -> {
						println(node.value)
						constructed.append(node.value)
						position = huffmanCode
					}

					is HuffmanCut<Char> -> TODO("cut ...")
				}
			}
			return constructed.toString()
		}

		val fields = mutableMapOf<String, String>()
		var dataLength = frame.length - ((if (priority) 5 else 0) + padLength)
		fun decodeInteger(octet: Int, prefixLength: Int): Int {
			var bitOffset = 0
			var mask = 0
			while (bitOffset < prefixLength) mask = mask or (1 shl bitOffset++)
			var prefix = octet and mask
			if (prefix == mask) {
				var m = 0
				do {
					val b = reader.readU8i()
					dataLength--
					prefix += ((b and 0b0_1111111) shl m)
					m += 7
				} while (b and 0b1_0000000 != 0)
			}
			return prefix
		}

		fun decodeString(): String {
			val o = reader.readU8i()
			val size = decodeInteger(o, 7)
			dataLength -= 1 + size
			return if (o and 0b1_0000000 != 0) decodeHuffman(reader.readN(size))
			else reader.readN(size).toString(Charsets.ISO_8859_1)
		}

		// TODO: dynamicTable [index - staticTable.size]
		while (dataLength > 0) {
			val o0 = reader.readU8i()
			dataLength--
			when {
				o0 and 0b1_0000000 != 0 -> {
					val index = decodeInteger(o0, 7)
					if (index == 0) TODO("Decoding error")
					val (key, value) = staticTable[index] ?: TODO("?? $index")
					fields[key] = value
				}

				o0 and 0b11_000000 == 0b01_000000 -> {
					val index = decodeInteger(o0, 6)
					val key = if (index == 0) decodeString()
					else staticTable[index]!!.first // TODO ?? $index
					val value = decodeString()
					addToDynamicTable(key, value)
					fields[key] = value
				}

				o0 and 0b1111_0000 == 0 || o0 and 0b1111_0000 == 0b1_0000 -> {
					val index = decodeInteger(o0, 4)
					val key = if (index == 0) decodeString()
					else staticTable[index]!!.first // TODO ?? $index
					fields[key] = decodeString()
				}

				o0 and 0b111_00000 == 0b001_00000 -> {
					val size = decodeInteger(o0, 5)
					if (size > localSetMaxHeaderTableSize) TODO("Decomp error")
					hPackMaxTableSize = size
					updateHPACKTableSize()
				}

				else -> TODO("? ${o0.toString(2).padStart(8, '0')}")
			}
		}
		val newState = HTTP2ConnectionState(peerInitialWindowSize)
		connectionStreams[frame.streamIdentifier] = newState
		if (endStream) newState.closed = HTTP2ConnectionState.CloseState.REMOTE
		reader.skip(padLength.toLong())
		streamHeaders.add(frame.streamIdentifier to fields)
		return true
	}

	private fun managingThread() {
		val initialFrame = readFrame()
		if (initialFrame !is HTTP2Frame || initialFrame.type.enum != HTTP2StandardFrameTypes.SETTINGS) {
			signalGoAway(MappedEnumeration(HTTP2StandardErrorCodes.PROTOCOL_ERROR), byteArrayOf())
			return
		}
		writeSettingsFrame()
		if (!interpretSettingsFrame(initialFrame)) return
		while (true) {
			val frame = readFrame() as HTTP2Frame // TODO over size
			when (frame.type.enum) {
				HTTP2StandardFrameTypes.WINDOW_UPDATE -> if (!interpretWindowUpdate(frame)) return
				HTTP2StandardFrameTypes.HEADERS -> if (!interpretHeadersFrame(frame)) return
				HTTP2StandardFrameTypes.SETTINGS -> if (!interpretSettingsFrame(frame)) return

				else -> {
					println(frame)
					Thread.sleep(9999999)
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