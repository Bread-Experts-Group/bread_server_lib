package org.bread_experts_group.coder.format.parse.id3.frame

import org.bread_experts_group.coder.Flaggable
import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream
import java.util.*
import kotlin.enums.EnumEntries

open class ID3Frame<T>(
	override val tag: String,
	baseFlags: EnumEntries<T>,
	flags: Int,
	val data: ByteArray
) : Tagged<String>, Writable where T : Enum<T>, T : Flaggable {
	val flags: EnumSet<T> = baseFlags.from(flags)
	override fun toString(): String = "ID3Frame.\"$tag\"$flags[#${data.size}]"
	override fun write(stream: OutputStream) = TODO("ID3 Writing")
}