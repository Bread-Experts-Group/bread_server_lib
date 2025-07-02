package org.bread_experts_group.coder.format.id3.frame

import org.bread_experts_group.coder.Flaggable
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream
import kotlin.enums.EnumEntries

open class ID3Frame<T>(
	override val tag: String,
	baseFlags: EnumEntries<T>,
	flags: Int,
	val data: ByteArray
) : Tagged<String>, Writable where T : Enum<T>, T : Flaggable {
	val flags: Set<T> = baseFlags.filter { flags.toLong() and it.position == it.position }.toSet()
	override fun toString(): String = "ID3Frame.\"$tag\"$flags[#${data.size}]"
	override fun write(stream: OutputStream) = TODO("ID3 Writing")
}