package org.bread_experts_group.taggart.png

enum class PNGDisposeOperation(val code: Int) {
	APNG_DISPOSE_OP_NONE(0),
	APNG_DISPOSE_OP_BACKGROUND(1),
	APNG_DISPOSE_OP_PREVIOUS(2);

	companion object {
		val mapping: Map<Int, PNGDisposeOperation> = entries.associateBy(PNGDisposeOperation::code)
	}
}