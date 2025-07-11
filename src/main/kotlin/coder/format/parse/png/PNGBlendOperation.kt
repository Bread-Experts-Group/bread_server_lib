package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGBlendOperation(
	override val id: Int,
	override val tag: String
) : Mappable<PNGBlendOperation, Int> {
	APNG_BLEND_OP_SOURCE(0, "Blend Source"),
	APNG_BLEND_OP_OVER(1, "Blend Over"),
	OTHER(-1, "Other");

	override fun other(): PNGBlendOperation? = OTHER
	override fun toString(): String = stringForm()
}