package org.bread_experts_group.coder.format.parse.mp3.frame.header

import org.bread_experts_group.coder.Mappable

enum class LayerDescription(override val id: Int, override val tag: String) : Mappable<LayerDescription, Int> {
	LAYER_3(1, "Layer III"),
	LAYER_2(2, "Layer II"),
	LAYER_1(3, "Layer I");

	override fun toString(): String = stringForm()
}