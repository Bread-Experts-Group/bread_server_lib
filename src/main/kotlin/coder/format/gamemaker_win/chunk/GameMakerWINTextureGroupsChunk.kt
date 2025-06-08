package org.bread_experts_group.coder.format.gamemaker_win.chunk

import org.bread_experts_group.coder.format.gamemaker_win.structure.GameMakerWINTextureGroup

data class GameMakerWINTextureGroupsChunk(
	override val offset: Long,
	val groups: List<GameMakerWINTextureGroup>
) : GameMakerWINChunk("TGIN", offset) {
	override fun toString(): String = super.toString() + "[${groups.size} texture group(s)]"
}