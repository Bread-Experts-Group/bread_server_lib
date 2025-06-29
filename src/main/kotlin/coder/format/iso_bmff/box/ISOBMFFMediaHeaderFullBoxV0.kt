package org.bread_experts_group.coder.format.iso_bmff.box

import java.time.ZonedDateTime
import java.util.*

class ISOBMFFMediaHeaderFullBoxV0(
	override val flags: Int,
	val creationTime: ZonedDateTime,
	val modificationTime: ZonedDateTime,
	val timescale: Int,
	val duration: Int,
	val language: Locale?,
	val padding: Int
) : ISOBMFFBox("mdhd", byteArrayOf()), ISOBMFFFullBox {
	override val version: Int = 0
	override fun toString(): String = "ISOBMFFBox.\"$tag\"[$creationTime, $modificationTime" +
			", timescale: 1/$timescale of a second, duration: $duration [${duration * (1.0 / timescale)}s]" +
			", language: ${language?.displayLanguage}]" + fullBoxString()
}