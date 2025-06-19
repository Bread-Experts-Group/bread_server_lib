package org.bread_experts_group.coder.format.elf.header

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

abstract class ELFGeneralHeader : Tagged<Nothing?>, Writable {
	override val tag: Nothing? = null
	override fun write(stream: OutputStream): Unit = throw UnsupportedOperationException()
}