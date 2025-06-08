package org.bread_experts_group.coder.format.iso_bmff.box

import java.time.ZonedDateTime

class ISOMBFFMovieHeaderBoxV1(
	val flags: Int,
	val creationTime: ZonedDateTime,
	val modificationTime: ZonedDateTime,
	val timescale: Int,
	val duration: Long,
	val preferredRate: Double,
	val preferredVolume: Double,
	val reserved: ByteArray,
	val matrix: IntArray,
	val predefined: ByteArray,
	val nextTrackID: Int
) : ISOBMFFBox("mvhd", byteArrayOf()) {
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[flags: $flags, $creationTime, $modificationTime" +
			", timescale: 1/$timescale of a second, duration: $duration [${duration * (1.0 / timescale)}s]" +
			", preferredRate: $preferredRate, preferredVolume: $preferredVolume, nextTrackID: $nextTrackID]"
}