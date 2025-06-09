package org.bread_experts_group.coder.format.iso_bmff.box

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

open class ISOBMFFBox(
	override val tag: String,
	val data: ByteArray
) : Writable, Tagged<String> {
	init {
		require(tag.length == 4) { "Name must be exactly 4 characters long" }
	}

	override fun computeSize(): Long = data.size.toLong()
	override fun write(stream: OutputStream) {
		stream.write32(computeSize())
		stream.writeString(tag, Charsets.US_ASCII)
		stream.write(data)
	}

	override fun toString(): String = "ISOBMFFBox.\"$tag\"[${data.size}]"
}