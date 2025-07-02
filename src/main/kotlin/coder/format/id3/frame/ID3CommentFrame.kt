package org.bread_experts_group.coder.format.id3.frame

import java.util.*

class ID3CommentFrame(
	tag: String,
	flags: Int,
	val encoding: ID3TextEncoding,
	val language: Locale,
	val short: String,
	val text: String
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$encoding.$language.\"$short\".\"$text\"]"
}