package org.bread_experts_group.coder.format.parse.flac.block

class FLACVorbisCommentMetadataBlock(
	val vendor: String,
	val comments: List<String>
) : FLACMetadataBlock(FLACBlockType.VORBIS_COMMENT, byteArrayOf()) {
	override fun toString(): String = "FLACVorbisCommentMetadataBlock[\"$vendor\", [${comments.size}]$comments]"
}