package org.bread_experts_group.coder.format.parse.id3.frame

import org.bread_experts_group.coder.format.parse.id3.ID3HeaderFlags

class ID3Header(
	val major: Int,
	val minor: Int,
	flags: Int
) : ID3Frame<ID3HeaderFlags>("ID3", ID3HeaderFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[v$major.$minor]"
}