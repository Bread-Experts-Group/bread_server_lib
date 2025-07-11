package org.bread_experts_group.coder.format.parse.flac.block

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

abstract class FLACBlock(
	override val tag: FLACBlockType,
	val data: ByteArray
) : Writable, Tagged<FLACBlockType> {
	override fun toString(): String = "FLACBlock.$tag[#${data.size}]"
	override fun write(stream: OutputStream) = throw IllegalStateException("Write FLACBlock")
}