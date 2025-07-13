package org.bread_experts_group.coder.format.parse.tiff

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.ValidationException
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFExifOffset
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFStructure
import org.bread_experts_group.coder.format.parse.tiff.structure.TIFFTextStructure
import org.bread_experts_group.hex
import org.bread_experts_group.stream.*
import java.io.InputStream

class TIFFParser(
	from: ByteArray,
	basis: TIFFParser? = null,
	skip: Long = 0
) : Parser<TIFFStructureIdentifier, TIFFStructure, InputStream>(
	"Tag Image File Format",
	from.inputStream()
) {
	private val eRead32ul: (InputStream) -> Long
	private val eRead16ui: (InputStream) -> Int
	private val size: Int

	init {
		if (basis != null) {
			eRead32ul = basis.eRead32ul
			eRead16ui = basis.eRead16ui
			size = basis.size
			rawStream.skip(skip)
		} else {
			size = rawStream.available()
			when (val order = fqIn.read16ui()) {
				0x4949 -> {
					eRead32ul = { it.read32().le().toLong() and 0xFFFFFFFF }
					eRead16ui = { it.read16().le().toInt() and 0xFFFF }
				}

				0x4D4D -> {
					eRead32ul = InputStream::read32ul
					eRead16ui = InputStream::read16ui
				}

				else -> throw InvalidInputException("Unknown byte-order format [${hex(order.toUShort())}]")
			}
			val magic = eRead16ui(fqIn)
			if (magic != 0x002A) throw InvalidInputException("Bad magic number [${hex(magic.toUShort())}]")
		}
	}

	override fun responsibleStream(of: TIFFStructure): InputStream = fqIn
	private val structures = ArrayDeque<TIFFStructure>()
	override fun readBase(compound: CodingCompoundThrowable): TIFFStructure? {
		if (structures.isNotEmpty()) return structures.removeFirst()
		val next = eRead32ul(fqIn)
		if (next == 0L) {
			fqIn.skipNBytes(rawStream.available().toLong())
			return null
		}
		rawStream.reset()
		rawStream.skip(next)
		repeat(eRead16ui(fqIn)) {
			structures.add(
				TIFFStructure(
					TIFFStructureIdentifier.entries.id(eRead16ui(fqIn)),
					TIFFDataType.entries.id(eRead16ui(fqIn)),
					eRead32ul(fqIn),
					eRead32ul(fqIn),
					(size - rawStream.available()).toLong()
				)
			)
		}
		return null
	}

	init {
		addParser(TIFFStructureIdentifier.EXIF_OFFSET) { _, structure, compound ->
			if (structure.items > 1) compound.addThrown(
				ValidationException("Exif Offset with multiple entries")
			)
			TIFFExifOffset(
				TIFFParser(
					from,
					this,
					structure.location - 4
				),
				structure.items, structure.offset, structure.location
			)
		}
		addPredicateParser({
			it.dataType == TIFFDataType.STRING || it.tag == TIFFStructureIdentifier.USER_COMMENT
		}) { _, structure, _ ->
			val newStream = FailQuickInputStream(from.inputStream())
			newStream.skip(structure.offset)
			TIFFTextStructure(
				structure.tag,
				structure.items, structure.offset, structure.location,
				newStream.readString()
			)
		}
	}
}