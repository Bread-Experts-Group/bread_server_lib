package org.bread_experts_group.coder.format.mp3

class ID3Header(
	val version: String,
	val unSynchronisation: Boolean,
	val extended: Boolean,
	val experimental: Boolean,
	val headerSize: Int,
	val headerOffset: String,
	val headerOffsetInt: Int
) {
	override fun toString(): String =
		"[ID3 Header] Version: $version," +
				" UnSynchronisation: $unSynchronisation," +
				" Extended: $extended," +
				" Experimental: $experimental," +
				" Header size: $headerSize" +
				" Header offset: $headerOffset"
}