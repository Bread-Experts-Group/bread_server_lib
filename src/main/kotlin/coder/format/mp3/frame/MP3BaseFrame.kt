package org.bread_experts_group.coder.format.mp3.frame

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

sealed class MP3BaseFrame : Tagged<Nothing?>, Writable {
	override val tag: Nothing? = null
	override fun write(stream: OutputStream) = TODO("MP3 Writing")
	override fun toString(): String = "MP3BaseFrame[]"
}