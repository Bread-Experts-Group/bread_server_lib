package org.bread_experts_group.coder.format.parse.id3.frame

import org.bread_experts_group.coder.format.parse.id3.ID3GenericFlags
import org.bread_experts_group.coder.format.parse.id3.ID3TextEncoding

class ID3TextFrame(
	tag: String,
	flags: Int,
	val encoding: ID3TextEncoding,
	val text: Array<String>
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$encoding.${text.contentToString()}]"
}