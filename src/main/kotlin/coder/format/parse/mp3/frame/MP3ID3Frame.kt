package org.bread_experts_group.coder.format.parse.mp3.frame

import org.bread_experts_group.coder.format.parse.id3.ID3Parser

class MP3ID3Frame(val id3: ID3Parser) : MP3BaseFrame() {
	override fun toString(): String = "MP3ID3Frame[$id3]"
}