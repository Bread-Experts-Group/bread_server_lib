package org.bread_experts_group.coder.format.parse.mp3.frame.header

import org.bread_experts_group.coder.format.parse.InvalidInputException

fun mp3SampleRate(index: Int, versionID: MPEGAudioVersionID) = when (versionID) {
	MPEGAudioVersionID.VERSION_1 -> when (index) {
		0 -> 44100
		1 -> 48000
		2 -> 32000
		else -> throw InvalidInputException("$versionID: unsupported $index")
	}

	MPEGAudioVersionID.VERSION_2 -> when (index) {
		0 -> 22050
		1 -> 24000
		2 -> 16000
		else -> throw InvalidInputException("$versionID: unsupported $index")
	}

	MPEGAudioVersionID.VERSION_2_5 -> when (index) {
		0 -> 11025
		1 -> 12000
		2 -> 8000
		else -> throw InvalidInputException("$versionID: unsupported $index")
	}
}