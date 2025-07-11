package org.bread_experts_group.coder.format.parse.iso_bmff.box

import java.io.OutputStream
import java.time.ZonedDateTime

class ISOMBFFMovieHeaderBoxV0(
	override val flags: Int,
	val creationTime: ZonedDateTime,
	val modificationTime: ZonedDateTime,
	val timescale: Int,
	val duration: Int,
	val preferredRate: Double,
	val preferredVolume: Double,
	val reserved: ByteArray,
	val matrix: IntArray,
	val predefined: ByteArray,
	val nextTrackID: Int
) : ISOBMFFBox("mvhd", byteArrayOf()), ISOBMFFFullBox {
	override val version: Int = 0
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[$creationTime, $modificationTime" +
			", timescale: 1/$timescale of a second, duration: $duration [${duration * (1.0 / timescale)}s]" +
			", preferredRate: $preferredRate, preferredVolume: $preferredVolume, nextTrackID: $nextTrackID]" +
			fullBoxString()

	override fun computeSize(): Long = TODO("V0 Size")
	override fun write(stream: OutputStream) {
		super.write(stream)
		TODO("V0")
	}
}