package org.bread_experts_group.api.coding.feature.jvm

import org.bread_experts_group.generic.Flaggable.Companion.from
import org.bread_experts_group.generic.Flaggable.Companion.raw
import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.api.coding.feature.PortableNetworkGraphicsCodingFeature
import org.bread_experts_group.api.coding.png.*
import org.bread_experts_group.api.feature.ImplementationSource
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class JVMPortableNetworkGraphicsCodingFeature : PortableNetworkGraphicsCodingFeature() {
	override val source: ImplementationSource = ImplementationSource.JVM_EMULATED
	private val magic = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)

	override fun read(
		from: org.bread_experts_group.generic.io.reader.SequentialDataSource,
		vararg features: PNGReadingFeatureIdentifier
	): List<PNGReadingDataIdentifier> {
		if (features.contains(StandardPNGReadingFeatures.CHECK_MAGIC)) {
			if (!from.readN(8).contentEquals(magic)) return emptyList()
			return listOf(StandardPNGReadingFeatures.CHECK_MAGIC)
		}
		val supportedFeatures = mutableListOf<PNGReadingDataIdentifier>()
		val header = features.firstNotNullOfOrNull { it as? PNGChunk.Header }
		val length = from.readU32l()
		when (val type = from.readU32l()) {
			0x49484452L if (features.contains(StandardPNGReadingFeatures.CHUNK_HEADER)) -> {
				// https://www.w3.org/TR/png-3/#11IHDR
				val w = from.readS32()
				val h = from.readS32()
				val bitDepth = from.readU8i()
				val colorType = PNGChunk.Header.ColorType.entries.from(from.readU8i())
				val compressionMethod = PNGChunk.Header.CompressionMethod.entries.id(from.readU8k())
				val filterMethod = PNGChunk.Header.FilterMethod.entries.id(from.readU8k())
				val interlaceMethod = PNGChunk.Header.InterlaceMethod.entries.id(from.readU8k())
				from.skip(length - 13)
				from.readS32() // TODO CRC32
				supportedFeatures.add(
					PNGChunk.Header(
						w, h,
						bitDepth, colorType,
						compressionMethod, filterMethod, interlaceMethod
					)
				)
			}

			0x504C5445L if (features.contains(StandardPNGReadingFeatures.CHUNK_PALETTE)) -> {
				val palette = IntArray((length / 3).toInt())
				for (i in palette.indices) {
					var rgb = 0x00000000
					rgb = rgb or (from.readU8i() shl 16)
					rgb = rgb or (from.readU8i() shl 8)
					rgb = rgb or from.readU8i()
					palette[i] = rgb
				}
				from.skip(length - (palette.size * 3))
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.Palette(palette))
			}

			0x49444154L if (features.contains(StandardPNGReadingFeatures.CHUNK_IMAGE_DATA)) -> {
				val data = from.readN(length.toInt())
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.ImageData(data))
			}

			0x74524E53L if (
					features.contains(StandardPNGReadingFeatures.CHUNK_TRANSPARENCY) &&
							header != null && header.colorType.raw() == 0L
					) -> {
				supportedFeatures.add(header)
				val grey = from.readU16i()
				from.skip(length - 2)
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.Transparency.Grey(grey))
			}

			0x74524E53L if (
					features.contains(StandardPNGReadingFeatures.CHUNK_TRANSPARENCY) &&
							header != null && header.colorType.raw() == 2L
					) -> {
				supportedFeatures.add(header)
				val r = from.readU16i()
				val g = from.readU16i()
				val b = from.readU16i()
				from.skip(length - 6)
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.Transparency.TrueColor(r, g, b))
			}

			0x74524E53L if (
					features.contains(StandardPNGReadingFeatures.CHUNK_TRANSPARENCY) &&
							header != null && header.colorType.raw() == 3L
					) -> {
				supportedFeatures.add(header)
				val palette = ByteArray(length.toInt())
				for (i in 0 until palette.size) palette[i] = from.readS8()
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.Transparency.Palette(palette))
			}

			0x6163544CL if (features.contains(StandardPNGReadingFeatures.CHUNK_ANIMATION_CONTROL)) -> {
				val frames = from.readS32()
				val playCount = from.readS32()
				from.skip(length - 8)
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.AnimationControl(frames, playCount))
			}

			0x6663544CL if (features.contains(StandardPNGReadingFeatures.CHUNK_FRAME_CONTROL)) -> {
				val sequence = from.readS32()
				val w = from.readS32()
				val h = from.readS32()
				val x = from.readS32()
				val y = from.readS32()
				val delayNum = from.readU16i()
				val delayDen = from.readU16i()
				val disposeOp = from.readU8k()
				val blendOp = from.readU8k()
				from.skip(length - 26)
				from.readS32() // TODO CRC32
				supportedFeatures.add(
					PNGChunk.FrameControl(
						sequence,
						w, h, x, y,
						(delayNum.toDouble() / (if (delayDen == 0) 100 else delayDen)).toDuration(DurationUnit.SECONDS),
						PNGChunk.FrameControl.DisposalOperation.entries.id(disposeOp),
						PNGChunk.FrameControl.BlendOperation.entries.id(blendOp)
					)
				)
			}

			0x66644154L if (features.contains(StandardPNGReadingFeatures.CHUNK_FRAME_DATA)) -> {
				val sequence = from.readS32()
				val data = from.readN(length.toInt() - 4)
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.FrameData(sequence, data))
			}

			0x49454E44L -> { // https://www.w3.org/TR/png-3/#11IEND
				from.skip(length)
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.End())
			}

			else if (features.contains(StandardPNGReadingFeatures.CHUNK_GENERIC)) -> {
				val data = from.readN(length.toInt())
				from.readS32() // TODO CRC32
				supportedFeatures.add(PNGChunk.Generic(type.toInt(), data))
			}

			else -> from.skip(length + 4)
		}
		return supportedFeatures
	}

	override fun write(
		into: org.bread_experts_group.generic.io.reader.SequentialDataSink,
		vararg features: PNGWritingFeatureIdentifier
	): List<PNGWritingDataIdentifier> {
		TODO("Not yet implemented")
	}
}