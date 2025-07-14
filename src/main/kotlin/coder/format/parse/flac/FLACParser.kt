package org.bread_experts_group.coder.format.parse.flac

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.InvalidInputException
import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.flac.block.*
import org.bread_experts_group.stream.*
import java.io.InputStream

class FLACParser : Parser<FLACBlockType, FLACBlock, InputStream>(
	"Free Lossless Audio Codec",
	InputStream::class
) {
	private var audioData = false
	override fun readBase(compound: CodingCompoundThrowable): FLACBlock? {
		val header = fqIn.read32u()
		if (!audioData) {
			val blockType = FLACBlockType.entries.id(((header shr 24) and 0b0111111u).toInt())
			val blockData = fqIn.readNBytes((header and 0b11111111_11111111_11111111u).toInt())
			audioData = header shr 31 != 0u
			return FLACMetadataBlock(blockType, blockData)
		}
		if ((header shr 17) != 0b111111_111111100u) throw InvalidInputException("FLAC alignment failure [$header]")
		val blockingStrategy = (header shr 16) and 0b1u
		val blockSize = (header shr 12) and 0b1111u
		val sampleRate = (header shr 8) and 0b1111u
		val channelAssignment = (header shr 4) and 0b1111u
		val sampleSize = (header shr 1) and 0b111u
		TODO("!")
	}

	init {
		addParser(FLACBlockType.STREAM_INFO) { stream, _, _ ->
			val minBlock = stream.read16ui()
			val maxBlock = stream.read16ui()
			val minFrame = stream.read24()
			val maxFrame = stream.read24()
			val packed = stream.read64()
			FLACStreamInfoMetadataBlock(
				minBlock, maxBlock,
				minFrame, maxFrame,
				(packed shr 44).toInt(),
				((packed shr 41) and 0b111).toInt() + 1,
				((packed shr 36) and 0b11111).toInt() + 1,
				packed and 0b1111_11111111_11111111_11111111_11111111,
				stream.readNBytes(16)
			)
		}
		addParser(FLACBlockType.SEEK_TABLE) { stream, _, _ ->
			FLACSeekTableMetadataBlock(
				buildList {
					while (stream.available() > 0) {
						add(
							FLACSeekPoint(
								stream.read64(),
								stream.read64(),
								stream.read16ui()
							)
						)
					}
				}
			)
		}
		addParser(FLACBlockType.VORBIS_COMMENT) { stream, _, _ ->
			FLACVorbisCommentMetadataBlock(
				stream.readString(stream.read32().le()),
				List(stream.read32().le()) {
					stream.readString(stream.read32().le())
				}
			)
		}
		addParser(FLACBlockType.PICTURE) { stream, _, _ ->
			FLACPictureMetadataBlock(
				FLACPictureType.entries.id(stream.read32()),
				stream.readString(stream.read32()),
				stream.readString(stream.read32()),
				stream.read32(),
				stream.read32(),
				stream.read32(),
				stream.read32(),
				stream.readNBytes(stream.read32())
			)
		}
	}

	override fun responsibleStream(of: FLACBlock): InputStream = of.data.inputStream()
	override fun inputInit() {
		val magic = fqIn.readString(4, Charsets.US_ASCII)
		if (magic != "fLaC") throw InvalidInputException("Magic incorrect [$magic] != [fLaC]")
	}
}