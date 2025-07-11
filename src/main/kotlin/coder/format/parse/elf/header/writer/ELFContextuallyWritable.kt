package org.bread_experts_group.coder.format.parse.elf.header.writer

import org.bread_experts_group.coder.format.parse.elf.header.ELFHeader
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

interface ELFContextuallyWritable : Tagged<Nothing?>, Writable {
	override fun write(stream: OutputStream) = throw UnsupportedOperationException()

	context(stream: OutputStream, header: ELFHeader, dataPosition: Long)
	fun write()
}