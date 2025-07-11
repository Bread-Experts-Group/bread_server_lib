package org.bread_experts_group.coder.format.parse.mp3.frame.header

import org.bread_experts_group.coder.Mappable

enum class ChannelMode(override val id: Int, override val tag: String) : Mappable<ChannelMode, Int> {
	STEREO(0, "Stereo"),
	JOINT_STEREO(1, "Joint Stereo"),
	DUAL_CHANNEL(2, "Dual Channel"),
	SINGLE_CHANNEL(3, "Single Channel");

	override fun toString(): String = stringForm()
}