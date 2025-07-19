package org.bread_experts_group.coder.format.parse.nbt.tag

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

abstract class NBTTag(override val tag: NBTTagType) : Tagged<NBTTagType>, Writable {
	override fun toString(): String = "NBTTag.$tag"
	override fun write(stream: OutputStream) = TODO("NYI")
}