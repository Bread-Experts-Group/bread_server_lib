package org.bread_experts_group.coder.format.mp3.frame.header

enum class MP3Emphasis {
	NONE, FIFTY_FIFTEEN_MS, RESERVED, CCIT_J_17;

	companion object {
		fun get(index: Int): MP3Emphasis = when (index) {
			0 -> NONE
			1 -> FIFTY_FIFTEEN_MS
			2 -> RESERVED
			3 -> CCIT_J_17
			else -> throw IllegalStateException()
		}
	}

	override fun toString(): String = when (this) {
		NONE -> "None"
		FIFTY_FIFTEEN_MS -> "50/15 ms"
		RESERVED -> "Reserved"
		CCIT_J_17 -> "CCIT J.17"
	}
}