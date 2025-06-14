package org.bread_experts_group.coder.format.png

enum class PNGBlendOperation(val code: Int) {
	APNG_BLEND_OP_SOURCE(0),
	APNG_BLEND_OP_OVER(1);

	companion object {
		val mapping: Map<Int, PNGBlendOperation> = entries.associateBy(PNGBlendOperation::code)
	}
}