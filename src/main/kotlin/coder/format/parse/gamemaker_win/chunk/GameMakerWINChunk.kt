package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

open class GameMakerWINChunk(
	override val tag: String,
	open val offset: Long
) : Writable, Tagged<String> {
	var length: Int = 0

	init {
		require(tag.length == 4) { "Name must be exactly 4 characters long" }
	}

	override fun computeSize(): Long {
		TODO("Not yet implemented")
	}

	override fun toString(): String = "${this::class.java.simpleName}.\"$tag\"@$offset[$length]"
	override fun write(stream: OutputStream) {
		TODO("Not yet implemented")
	}
}