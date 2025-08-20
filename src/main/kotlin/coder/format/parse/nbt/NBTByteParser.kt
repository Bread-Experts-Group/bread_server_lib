package org.bread_experts_group.coder.format.parse.nbt

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.format.parse.ByteParser
import org.bread_experts_group.coder.format.parse.CodingCompoundThrowable
import org.bread_experts_group.coder.format.parse.nbt.tag.*
import org.bread_experts_group.io.reader.ReadingByteBuffer
import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel

class NBTByteParser : ByteParser<NBTTagType, NBTTag, ReadableByteChannel>("Named Binary Tag") {
	override fun responsibleChannel(of: NBTTag): ReadableByteChannel = channel

	private lateinit var readable: ReadingByteBuffer
	private fun nextString(): String {
		val bytes = ByteArray(readable.u16i32())
		readable.get(bytes)
		return bytes.toString(Charsets.UTF_8)
	}

	private fun readInCompoundTag(lim: Int = Int.MAX_VALUE): Map<String, NBTTag> = buildMap {
		var pos = 0
		while (true) {
			if (pos == lim) break
			val next = NBTTagType.entries.id(readable.u8()).enum!!
			if (next == NBTTagType.END_OF_COMPOUND) break
			val name = nextString()
			set(name, nextTag(next))
			pos++
		}
	}

	private fun nextTag(n: NBTTagType): NBTTag = when (n) {
		NBTTagType.SHORT -> NBTShortTag(readable.i16())
		NBTTagType.UTF_8 -> NBTStringTag(nextString())
		NBTTagType.LIST -> {
			val type = NBTTagType.entries.id(readable.u8()).enum!!
			NBTListTag(
				type,
				List(readable.i32()) { nextTag(type) }
			)
		}

		NBTTagType.COMPOUND -> NBTCompoundTag(readInCompoundTag())
		else -> throw UnsupportedOperationException("Tag #$n")
	}

	var readTop = false
	override fun readBase(compound: CodingCompoundThrowable): NBTTag? {
		if (readTop) throw EOFException()
		readTop = true
		return NBTCompoundTag(readInCompoundTag(1))
	}

	fun setInputReading(from: ReadingByteBuffer): ByteParser<NBTTagType, NBTTag, ReadableByteChannel> {
		readable = from
		return this
	}

	override fun inputInit() {
		readable = ReadingByteBuffer(channel, ByteBuffer.allocateDirect(1024), null)
	}
}