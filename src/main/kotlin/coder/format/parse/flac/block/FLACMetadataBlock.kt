package org.bread_experts_group.coder.format.parse.flac.block

open class FLACMetadataBlock(
	tag: FLACBlockType,
	data: ByteArray
) : FLACBlock(tag, data) {
	init {
		if (tag == FLACBlockType.AUDIO_DATA) throw IllegalStateException("Parsing structural failure")
	}
}