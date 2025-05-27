package org.bread_experts_group.coder.format.iso_bmff.box

import org.bread_experts_group.Writable
import org.bread_experts_group.stream.write32
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

open class ISOBMFFBox(
	val name: String,
	val data: ByteArray
) : Writable {
	init {
		require(name.length == 4) { "Name must be exactly 4 characters long" }
	}

	override fun write(stream: OutputStream) {
		stream.write32(data.size)
		stream.writeString(name, Charsets.US_ASCII)
		stream.write(data)
	}

	override fun toString(): String = "ISOBMFFBox.\"$name\"[${data.size}]"
}