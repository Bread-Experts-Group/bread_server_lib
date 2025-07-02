package org.bread_experts_group.coder.format.mp3.frame.header

class MP3Header(
	val versionId: MPEGAudioVersionID,
	val layerDescription: LayerDescription,
	val protection: Boolean,
	val bitrate: MP3Bitrate,
	val sampleRate: MP3SampleRate,
	val padding: Boolean,
	val hasPrivateBit: Boolean,
	val channelMode: ChannelMode,
	val extension: String,
	val copyright: Boolean,
	val original: Boolean,
	val emphasis: MP3Emphasis
) {
	override fun toString(): String =
		"[MP3 Header]" +
				" Version ID: $versionId" +
				" Layer: $layerDescription" +
				" Protection: $protection" +
				" Bitrate: $bitrate" +
				" Sample rate: $sampleRate" +
				" Padding: $padding" +
				" Has private bit: $hasPrivateBit" +
				" Channel mode: $channelMode" +
				" Mode Extension: $extension" +
				" Copyright: $copyright" +
				" Original: $original" +
				" Emphasis: $emphasis"
}