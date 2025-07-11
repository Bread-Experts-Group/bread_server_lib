package org.bread_experts_group.coder.format.parse.flac.block

class FLACAudioFrameBlock(
	data: ByteArray
) : FLACBlock(FLACBlockType.AUDIO_DATA, data) {
	override fun toString(): String = "FLACAudioFrameBlock[#$data]"
}