package org.bread_experts_group.coder.format.mp3.frame

import org.bread_experts_group.coder.format.mp3.frame.header.MP3Header

class MP3Frame(
	val header: MP3Header,
	val data: ByteArray
) : MP3BaseFrame() {
	override fun toString(): String = "MP3Frame[$header][#${data.size}]"
}