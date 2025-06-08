package org.bread_experts_group.coder.format.gamemaker_win.structure

import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk

data class GameMakerWINAudio(
	val offset: Long,
	val chunks: List<RIFFChunk>
) {
	override fun toString(): String = "GameMakerWINAudio@$offset[${chunks.size}][$chunks]"
}