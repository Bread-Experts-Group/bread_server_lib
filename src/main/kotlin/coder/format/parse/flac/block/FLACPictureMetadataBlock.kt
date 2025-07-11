package org.bread_experts_group.coder.format.parse.flac.block

class FLACPictureMetadataBlock(
	val pictureType: FLACPictureType,
	val mimeType: String,
	val description: String,
	val width: Int,
	val height: Int,
	val colorDepth: Int,
	val colors: Int,
	data: ByteArray
) : FLACMetadataBlock(FLACBlockType.PICTURE, data) {
	override fun toString(): String = "FLACPictureMetadataBlock[$pictureType, $mimeType, \"$description\", " +
			"$width x $height, $colorDepth-bit, ${if (colors == 0) "sampled" else "indexed [$colors]"}][#${data.size}]"
}