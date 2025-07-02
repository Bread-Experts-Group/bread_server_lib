package org.bread_experts_group.coder.format.mp3.header

enum class LayerDescription {
	RESERVED, LAYER_3, LAYER_2, LAYER_1;

	companion object {
		fun get(index: Int): LayerDescription = when (index) {
			0 -> RESERVED
			1 -> LAYER_3
			2 -> LAYER_2
			3 -> LAYER_1
			else -> throw IllegalStateException()
		}
	}

	override fun toString(): String = when (this) {
		RESERVED -> "Reserved"
		LAYER_1 -> "Layer I"
		LAYER_2 -> "Layer II"
		LAYER_3 -> "Layer III"
	}
}