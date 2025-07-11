package org.bread_experts_group.coder.format.parse.id3.frame

import org.bread_experts_group.coder.format.parse.id3.ID3GenericFlags
import org.bread_experts_group.coder.format.parse.id3.ID3TextEncoding
import java.util.*

class ID3UnsynchronizedLyricsFrame(
	tag: String,
	flags: Int,
	val encoding: ID3TextEncoding,
	val language: Locale,
	val descriptor: String,
	val text: String
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$encoding.$language.\"$descriptor\".\"$text\"]"
}