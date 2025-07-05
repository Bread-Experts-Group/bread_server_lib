package org.bread_experts_group.coder.format.mp3.frame.header

import org.bread_experts_group.coder.Mappable

enum class MP3Emphasis(override var id: Int, override val tag: String) : Mappable<MP3Emphasis, Int> {
	NONE(0, "No Emphasis"),
	FIFTY_FIFTEEN(1, "50/15"),
	CCIT_J_17(3, "CCIT J.17");

	override fun toString(): String = stringForm()
}