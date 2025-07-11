package org.bread_experts_group.coder.format.parse.flac.block

data class FLACSeekPoint(
	val sample: Long,
	val offset: Long,
	val samples: Int
) {
	override fun toString(): String = "FLACSeekPoint[$sample@$offset, #$samples]"
}