package org.bread_experts_group.coder.format.parse.mp3.frame

import org.bread_experts_group.coder.format.parse.Parser
import org.bread_experts_group.coder.format.parse.id3.frame.ID3Frame
import java.io.InputStream

class MP3ID3Frame(val id3: Parser<String, ID3Frame<*>, InputStream>) : MP3BaseFrame() {
	override fun toString(): String = "MP3ID3Frame[$id3]"
}