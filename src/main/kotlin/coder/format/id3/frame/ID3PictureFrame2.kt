package org.bread_experts_group.coder.format.id3.frame

class ID3PictureFrame2(
	tag: String,
	flags: Int,
	val encoding: ID3TextEncoding,
	val imageType: String,
	val pictureType: ID3PictureType,
	val description: String,
	data: ByteArray
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, data) {
	override fun toString(): String = super.toString() + "[$encoding.$imageType.$pictureType.\"$description\"]"
}