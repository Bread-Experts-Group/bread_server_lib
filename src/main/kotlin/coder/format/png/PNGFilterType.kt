package org.bread_experts_group.taggart.png

enum class PNGFilterType(val code: Int) {
	ADAPTIVE(0);

	companion object {
		val mapping = entries.associateBy(PNGFilterType::code)
	}
}