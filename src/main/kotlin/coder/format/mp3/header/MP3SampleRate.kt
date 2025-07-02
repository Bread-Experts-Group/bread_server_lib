package org.bread_experts_group.coder.format.mp3.header

class MP3SampleRate(val index: Int, val versionID: MPEGAudioVersionID) {
	val raw: Int = when (versionID) {
		MPEGAudioVersionID.VERSION_1 -> when (index) {
			0 -> 44100
			1 -> 48000
			2 -> 32000
			else -> throw IllegalStateException()
		}

		MPEGAudioVersionID.VERSION_2 -> when (index) {
			0 -> 22050
			1 -> 24000
			2 -> 16000
			else -> throw IllegalStateException()
		}

		MPEGAudioVersionID.VERSION_2_5 -> when (index) {
			0 -> 11025
			1 -> 12000
			2 -> 8000
			else -> throw IllegalStateException()
		}

		else -> throw IllegalStateException()
	}

	override fun toString(): String = "$raw Hz"
}