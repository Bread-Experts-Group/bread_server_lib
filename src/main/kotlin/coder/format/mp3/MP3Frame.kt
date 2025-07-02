package org.bread_experts_group.coder.format.mp3

import org.bread_experts_group.coder.format.mp3.header.MP3Header
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

class MP3Frame(
	val header: MP3Header,
	val data: ByteArray
) : Tagged<Nothing?>, Writable {
	override val tag: Nothing? = null
	override fun write(stream: OutputStream) = throw UnsupportedOperationException()
}