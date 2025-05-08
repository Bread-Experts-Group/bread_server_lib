package org.bread_experts_group

import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer

interface CharacterWritable : Writable {
	override fun write(stream: OutputStream) = this.write(OutputStreamWriter(stream, Charsets.UTF_8))
	fun write(writer: Writer)
}