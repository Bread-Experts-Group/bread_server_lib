package org.bread_experts_group.coder.format.parse.gamemaker_win.structure

class GameMakerWINBytecode(
	val offset: Long,
	val name: String,
	val length: Long,
	val locals: Int,
	arguments: Int,
	val address: Long
) {
	val arguments: Int = arguments and 0x7FFF

	override fun toString(): String = "GameMakerWINBytecode@$offset[$name, # $length, $locals local(s), " +
			"$arguments argument(s), @ $address]"
}