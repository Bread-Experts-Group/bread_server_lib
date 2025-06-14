package org.bread_experts_group.coder.format.png

enum class PNGFilterType(val code: Int) {
	ADAPTIVE(0);

	companion object {
		val mapping = entries.associateBy(PNGFilterType::code)
	}
}