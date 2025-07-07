package org.bread_experts_group.coder.format.id3.frame

class ID3TextFrame(
	tag: String,
	flags: Int,
	val encoding: ID3TextEncoding,
	val text: Array<String>
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$encoding.${text.contentToString()}]"
}