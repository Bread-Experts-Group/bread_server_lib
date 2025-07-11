package org.bread_experts_group.coder.format.parse.mp3.frame.header

import org.bread_experts_group.coder.Mappable

enum class MPEGAudioVersionID(override val id: Int, override val tag: String) : Mappable<MPEGAudioVersionID, Int> {
	VERSION_2_5(0, "v2.5"),
	VERSION_2(2, "v2"),
	VERSION_1(3, "v1");

	override fun toString(): String = stringForm()
}