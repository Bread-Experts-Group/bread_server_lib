package org.bread_experts_group.coder.format.png

enum class PNGInterlaceType(val code: Int) {
	NONE(0),
	ADAM7(1);

	companion object {
		val mapping = entries.associateBy(PNGInterlaceType::code)
	}
}