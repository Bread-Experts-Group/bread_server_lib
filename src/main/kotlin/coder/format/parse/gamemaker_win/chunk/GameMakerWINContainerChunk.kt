package org.bread_experts_group.coder.format.parse.gamemaker_win.chunk

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult

class GameMakerWINContainerChunk(
	tag: String,
	override val offset: Long,
	val chunks: List<LazyPartialResult<GameMakerWINChunk, CodingException>>
) : GameMakerWINChunk(tag, offset) {
	override fun toString(): String = super.toString() + "[${chunks.size}]\n\t[${
		chunks.joinToString(",\n\t") { it.toString().replace("\n", "\n\t") }
	}]"
}