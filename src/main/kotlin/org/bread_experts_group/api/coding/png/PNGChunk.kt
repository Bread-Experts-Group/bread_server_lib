package org.bread_experts_group.api.coding.png

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.generic.MappedEnumeration
import java.util.*
import kotlin.time.Duration

open class PNGChunk(
	val identifier: Int
) : PNGReadingDataIdentifier {
	class Header(
		val w: Int,
		val h: Int,
		val bitDepth: Int,
		val colorType: EnumSet<ColorType>,
		val compressionMethod: MappedEnumeration<UByte, CompressionMethod>,
		val filterMethod: MappedEnumeration<UByte, FilterMethod>,
		val interlaceMethod: MappedEnumeration<UByte, InterlaceMethod>
	) : PNGChunk(0x49484452), PNGReadingFeatureIdentifier {
		enum class ColorType : Flaggable {
			INDEXED,
			TRUE_COLOR,
			ALPHA;

			override val position: Long = 1L shl ordinal
		}

		enum class CompressionMethod(
			override val id: UByte,
			override val tag: String
		) : Mappable<CompressionMethod, UByte> {
			DEFLATE(0u, "Deflate [RFC 1951] compression, sliding window max: 32,768 bytes");

			override fun toString(): String = stringForm()
		}

		enum class FilterMethod(
			override val id: UByte,
			override val tag: String
		) : Mappable<FilterMethod, UByte> {
			ADAPTIVE_B5(0u, "Adaptive filtering with 5 basic types");

			override fun toString(): String = stringForm()
		}

		enum class InterlaceMethod(
			override val id: UByte,
			override val tag: String
		) : Mappable<InterlaceMethod, UByte> {
			NONE(0u, "No interlacing"),
			ADAM7(1u, "Adam7 interlacing");

			override fun toString(): String = stringForm()
		}
	}

	class Palette(
		val palette: IntArray
	) : PNGChunk(0x504C5445)

	class ImageData(
		val data: ByteArray
	) : PNGChunk(0x49444154)

	class End : PNGChunk(0x49454E44)

	open class Transparency : PNGChunk(0x74524E53) {
		class Grey(val grey: Int) : Transparency()
		class TrueColor(val r: Int, val g: Int, val b: Int) : Transparency()
		class Palette(val palette: ByteArray) : Transparency()
	}

	class AnimationControl(
		val frames: Int,
		val playCount: Int
	) : PNGChunk(0x6163544C)

	class FrameControl(
		val sequence: Int,
		val w: Int,
		val h: Int,
		val x: Int,
		val y: Int,
		val delay: Duration,
		val disposeOperation: MappedEnumeration<UByte, DisposalOperation>,
		val blendOperation: MappedEnumeration<UByte, BlendOperation>
	) : PNGChunk(0x6663544C) {
		enum class DisposalOperation(
			override val id: UByte,
			override val tag: String
		) : Mappable<DisposalOperation, UByte> {
			APNG_DISPOSE_OP_NONE(0u, "No disposal"),
			APNG_DISPOSE_OP_BACKGROUND(1u, "Clear to background"),
			APNG_DISPOSE_OP_PREVIOUS(2u, "Clear to last frame");

			override fun toString(): String = stringForm()
		}

		enum class BlendOperation(
			override val id: UByte,
			override val tag: String
		) : Mappable<BlendOperation, UByte> {
			APNG_BLEND_OP_SOURCE(0u, "Overwrite"),
			APNG_BLEND_OP_OVER(1u, "Alpha Blend Over");

			override fun toString(): String = stringForm()
		}
	}

	class FrameData(
		val sequence: Int,
		val data: ByteArray
	) : PNGChunk(0x66644154)

	class Generic(
		identifier: Int,
		val data: ByteArray
	) : PNGChunk(identifier)
}