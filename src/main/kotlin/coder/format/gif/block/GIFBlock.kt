package org.bread_experts_group.coder.format.gif.block

import org.bread_experts_group.hex
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

open class GIFBlock(
	override val tag: Byte,
	val data: ByteArray
) : Tagged<Byte>, Writable {
	override fun write(stream: OutputStream): Nothing = throw UnsupportedOperationException()
	override fun toString(): String = "GIFBlock[${hex(tag.toUByte())}][${data.size}]"
}