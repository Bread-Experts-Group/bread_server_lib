package org.bread_experts_group.coder.format.mp3

class ID3Header(
	val identifier: String,
	val version: String,
	val unsynchronisation: Boolean,
	val extended: Boolean,
	val experimental: Boolean,
	val headerSize: Int,
	val headerOffset: String,
	val headerOffsetInt: Int
) {
	override fun toString(): String =
		"[ID3 Header] Version: $version," +
				" Unsynchronisation: $unsynchronisation," +
				" Extended: $extended," +
				" Experimental: $experimental," +
				" Header size: $headerSize" +
				" Header offset: $headerOffset"
}