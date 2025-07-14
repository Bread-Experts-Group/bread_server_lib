package org.bread_experts_group.coder.format.parse.tiff

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.ByteParser
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.ValidationException
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFExifOffset
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFStructure
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFTextStructure
import org.bread_experts_group.hex
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.WindowedSeekableByteChannel
import org.bread_experts_group.stream.readString
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.Channels
import java.nio.channels.SeekableByteChannel

class TIFFByteParser : ByteParser<TIFFStructureIdentifier, TIFFStructure, SeekableByteChannel>(
	"Tag Image File Format"
) {
	private var order: ByteOrder = ByteOrder.BIG_ENDIAN

	override fun responsibleChannel(of: TIFFStructure): SeekableByteChannel = WindowedSeekableByteChannel(
		channel,
		0, channel.size()
	)

	private val structures = ArrayDeque<TIFFStructure>()
	private val buffer12 = ByteBuffer.allocate(12)
	override fun readBase(compound: CodingCompoundThrowable): TIFFStructure? {
		if (structures.isNotEmpty()) return structures.removeFirst()
		buffer12.limit(4)
		channel.read(buffer12)
		buffer12.rewind()
		val next = buffer12.int.toLong() and 0xFFFFFFFF
		if (next == 0L) return null
		channel.position(next)
		buffer12.limit(2)
		buffer12.rewind()
		channel.read(buffer12)
		buffer12.rewind()
		repeat(buffer12.short.toInt() and 0xFFFF) {
			buffer12.clear()
			channel.read(buffer12)
			buffer12.flip()
			structures.add(
				TIFFStructure(
					TIFFStructureIdentifier.entries.id(buffer12.short.toInt() and 0xFFFF),
					TIFFDataType.entries.id(buffer12.short.toInt() and 0xFFFF),
					buffer12.int.toLong() and 0xFFFFFFFF,
					buffer12.int.toLong() and 0xFFFFFFFF,
					channel.position()
				)
			)
		}
		buffer12.rewind()
		return structures.removeFirst()
	}

	init {
		addParser(TIFFStructureIdentifier.EXIF_OFFSET) { window, structure, compound ->
			if (structure.items > 1) compound.addThrown(
				ValidationException("Exif Offset with multiple entries")
			)
			window.position(structure.location - 4)
			TIFFExifOffset(
				TIFFByteParser().internalInit(window, order),
				structure.items, structure.offset, structure.location
			)
		}
		addPredicateParser({
			it.dataType == TIFFDataType.STRING || it.tag == TIFFStructureIdentifier.USER_COMMENT
		}) { window, structure, _ ->
			val newStream = FailQuickInputStream(Channels.newInputStream(window))
			window.position(structure.offset)
			TIFFTextStructure(
				structure.tag,
				structure.items, structure.offset, structure.location,
				newStream.readString()
			)
		}
	}

	private fun internalInit(from: SeekableByteChannel, fromOrder: ByteOrder): TIFFByteParser {
		channel = from
		buffer12.order(fromOrder)
		return this
	}

	override fun inputInit() {
		val first4 = ByteBuffer.allocate(4)
		channel.read(first4)
		first4.rewind()
		order = when (val orderSign = first4.short.toInt() and 0xFFFF) {
			0x4949 -> ByteOrder.LITTLE_ENDIAN
			0x4D4D -> ByteOrder.BIG_ENDIAN
			else -> throw InvalidInputException("Unknown byte-order format [${hex(orderSign.toUShort())}]")
		}
		first4.order(order)
		buffer12.order(order)
		val magic = first4.short.toInt() and 0xFFFF
		if (magic != 0x002A) throw InvalidInputException("Bad magic number [${hex(magic.toUShort())}]")
	}
}