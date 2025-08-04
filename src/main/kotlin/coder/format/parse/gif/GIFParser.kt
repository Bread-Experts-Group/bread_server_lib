package org.bread_experts_group.coder.format.parse.gif

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.gif.block.*
import org.bread_experts_group.stream.ConsolidatedInputStream
import org.bread_experts_group.stream.le
import org.bread_experts_group.stream.read16
import org.bread_experts_group.stream.readString
import java.awt.Color
import java.io.InputStream
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GIFParser : Parser<Byte, GIFBlock, InputStream>(
	"Graphics Interchange Format",
	InputStream::class
) {
	private val preread = ArrayDeque<GIFBlock>()

	private fun readColorTable(size: Int) = List(size) {
		Color(fqIn.read(), fqIn.read(), fqIn.read())
	}

	@OptIn(ExperimentalContracts::class)
	private fun <T> ensureExistence(qualifier: Boolean, of: T?): Boolean {
		contract {
			returns(true) implies (of != null)
			returns(false) implies (of == null)
		}
		return if (qualifier) {
			if (of != null) true
			else throw NullPointerException("\"of\" must not be null if qualified")
		} else {
			if (of == null) false
			else throw IllegalStateException("\"of\" must be null if not qualified")
		}
	}

	override fun responsibleStream(of: GIFBlock): InputStream = fqIn
	override fun readBase(compound: CodingCompoundThrowable): GIFBlock =
		preread.removeFirstOrNull()
			?: GIFBlock(fqIn.read().toByte(), byteArrayOf())

	private fun readBlocks(): ConsolidatedInputStream {
		val consolidated = ConsolidatedInputStream(false)
		while (true) {
			val data = fqIn.readNBytes(fqIn.read())
			if (data.isEmpty()) break
			consolidated.streams.add(data.inputStream())
		}
		return consolidated
	}

	init {
		addParser(0x2C) { stream, block, _ ->
			val x = fqIn.read16().le().toInt()
			val y = fqIn.read16().le().toInt()
			val width = fqIn.read16().le().toInt()
			val height = fqIn.read16().le().toInt()
			val packed = fqIn.read()

			val localColorTable = (packed and 0b10000000) != 0
			val interlaced = (packed and 0b01000000) != 0
			val sorted = (packed and 0b00100000) != 0
			val localColorTableSize = 1 shl ((packed and 0b00000111) + 1)
			val colorTable = if (localColorTable) readColorTable(localColorTableSize) else null

			val lzwMCS = fqIn.read()
			GIFImageDescriptor(
				x, y, width, height,
				interlaced, sorted, colorTable,
				lzwMCS, readBlocks()
			)
		}
		addParser(0x21) { stream, block, _ ->
			val extension = fqIn.read()
			val size = fqIn.read()
			when (extension) {
				0xFF -> GIFApplicationExtensionBlock(
					fqIn.readString(8),
					fqIn.readNBytes(3),
					readBlocks()
				)

				0xF9 -> {
					val packed = stream.read()
					val delayTime = fqIn.read16().le() * 10L
					val transparentColorIndex = stream.read()
					fqIn.read() // Terminator
					GIFGraphicControlExtensionBlock(
						delayTime.toDuration(DurationUnit.MILLISECONDS),
						if (packed and 1 == 0) null else transparentColorIndex,
						GIFDisposalMethod.entries.id((packed and 0b00011100) ushr 2).enum,
						(packed ushr 1) and 1 == 1
					)
				}

				else -> GIFExtensionBlock(
					extension.toByte(),
					fqIn.readNBytes(size)
				)
			}
		}
	}

	override fun inputInit() {
		val signature = fqIn.readString(3)
		require(signature == "GIF") {
			"GIF signature mismatch; [\"${signature}\" =/= \"GIF\"]"
		}

		val version = fqIn.readString(3)
		require(version == "87a" || version == "89a") {
			"Unsupported GIF version; \"$version\", supported versions are [87a, 89a]"
		}

		// Logical Screen Descriptor
		run {
			val width = fqIn.read16().le().toInt()
			val height = fqIn.read16().le().toInt()
			val packed = fqIn.read()
			val backgroundColorIndex = fqIn.read()
			val pixelAspectRatio = fqIn.read()

			val globalColorTable = (packed and 0b10000000) != 0
			val bitDepth = ((packed and 0b01110000) ushr 4) + 1
			val colorsSorted = (packed and 0b00001000) != 0
			val globalColorTableSize = 1 shl ((packed and 0b00000111) + 1)
			val colorTable = if (globalColorTable) readColorTable(globalColorTableSize) else null
			val exists = ensureExistence(globalColorTable, colorTable)

			preread.addLast(
				GIFLogicalScreenDescriptorBlock(
					width, height,
					if (exists) colorTable[backgroundColorIndex] else null,
					bitDepth,
					colorsSorted,
					if (pixelAspectRatio > 0) (pixelAspectRatio.toFloat() + 15) / 64 else null,
					colorTable
				)
			)
		}
	}
}