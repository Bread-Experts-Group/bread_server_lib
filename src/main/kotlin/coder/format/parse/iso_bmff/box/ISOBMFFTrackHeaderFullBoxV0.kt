package org.bread_experts_group.coder.format.parse.iso_bmff.box

import java.time.ZonedDateTime

class ISOBMFFTrackHeaderFullBoxV0(
	override val flags: Int,
	val creationTime: ZonedDateTime,
	val modificationTime: ZonedDateTime,
	val trackID: Int,
	val reserved: Int,
	val duration: Int,
	val reserved2: IntArray,
	val layer: Int,
	val alternateGroup: Int,
	val volume: Double,
	val reserved3: Int,
	val matrix: IntArray,
	val width: Double,
	val height: Double
) : ISOBMFFBox("tkhd", byteArrayOf()), ISOBMFFFullBox {
	override val version: Int = 0
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[#$trackID, $creationTime, $modificationTime" +
			", duration: $duration, layer: $layer, alternate group: $alternateGroup" +
			", volume: $volume, $width x $height]" + fullBoxString()
}