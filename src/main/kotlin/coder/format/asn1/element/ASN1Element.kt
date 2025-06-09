package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

open class ASN1Element(
	override val tag: Int,
	open val data: ByteArray
) : Writable, Tagged<Int> {
	override fun toString(): String = "ASN1Element.$tag[${data.size}]"

	override fun computeSize(): Long = data.size.toLong()
	final override fun write(stream: OutputStream) {
		stream.write(tag)
		writeExtra(stream)
		stream.writeLength(computeSize().toInt())
	}

	protected fun OutputStream.writeLength(size: Int) {
		when {
			size < 0x80 -> this.write(size)
			size <= 0xFF -> {
				this.write(0x81)
				this.write(size)
			}

			else -> {
				this.write(0x82)
				this.write((size shr 8) and 0xFF)
				this.write(size and 0xFF)
			}
		}
	}

	open fun writeExtra(stream: OutputStream) {
		stream.writeLength(data.size)
		stream.write(data)
	}
}