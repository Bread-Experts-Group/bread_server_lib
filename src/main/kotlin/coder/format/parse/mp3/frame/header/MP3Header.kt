package org.bread_experts_group.coder.format.parse.mp3.frame.header

import org.bread_experts_group.formatMetric

data class MP3Header(
	val versionId: MPEGAudioVersionID,
	val layerDescription: LayerDescription,
	val protection: Boolean,
	val bitrate: Int,
	val sampleRate: Int,
	val padding: Boolean,
	val hasPrivateBit: Boolean,
	val channelMode: ChannelMode,
	val extension: ModeExtension,
	val copyright: Boolean,
	val original: Boolean,
	val emphasis: MP3Emphasis
) {
	override fun toString(): String = "MP3Header[$versionId, $layerDescription, $channelMode" +
			(if (channelMode == ChannelMode.JOINT_STEREO) " [$extension]" else "") + ", $emphasis, [" +
			buildList {
				if (protection) add("CHECKSUM")
				if (padding) add("PADDED")
				if (hasPrivateBit) add("PRIVATE")
				if (copyright) add("COPYRIGHT")
				if (original) add("ORIGINAL")
			}.joinToString(",") + "], $bitrate, ${sampleRate.toDouble().formatMetric()}Hz]"
}