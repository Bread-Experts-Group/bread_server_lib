package org.bread_experts_group.coder.format.mp3.header

enum class MPEGAudioVersionID {
	VERSION_2_5, RESERVED, VERSION_2, VERSION_1;

	companion object {
		fun get(index: Int): MPEGAudioVersionID = when (index) {
			0 -> VERSION_2_5
			1 -> RESERVED
			2 -> VERSION_2
			3 -> VERSION_1
			else -> throw IllegalStateException()
		}
	}

	override fun toString(): String = when (this) {
		RESERVED -> "Reserved"
		VERSION_1 -> "Version 1"
		VERSION_2 -> "Version 2"
		VERSION_2_5 -> "Version 2.5"
	}
}