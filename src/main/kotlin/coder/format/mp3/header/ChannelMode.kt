package org.bread_experts_group.coder.format.mp3.header

enum class ChannelMode {
	STEREO, JOINT_STEREO, DUAL_CHANNEL, SINGLE_CHANNEL;

	companion object {
		fun get(index: Int): ChannelMode = when (index) {
			0 -> STEREO
			1 -> JOINT_STEREO
			2 -> DUAL_CHANNEL
			3 -> SINGLE_CHANNEL
			else -> throw IllegalStateException()
		}
	}

	override fun toString(): String = when (this) {
		STEREO -> "Stereo"
		JOINT_STEREO -> "Joint stereo (Stereo)"
		DUAL_CHANNEL -> "Dual channel (2 mono channels)"
		SINGLE_CHANNEL -> "Single channel (mono)"
	}
}