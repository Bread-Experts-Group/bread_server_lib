package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.riff.chunk.RIFFChunk

data class GameMakerWINAudio(
	val offset: Long,
	val chunks: List<LazyPartialResult<RIFFChunk, CodingException>>
) {
	override fun toString(): String = "GameMakerWINAudio@$offset[${chunks.size}][$chunks]"
}