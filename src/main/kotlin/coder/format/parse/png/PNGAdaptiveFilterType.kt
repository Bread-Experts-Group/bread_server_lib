package org.bread_experts_group.coder.format.parse.png

enum class PNGAdaptiveFilterType(val code: Int) {
	NONE(0),
	SUBTRACT(1),
	UP(2),
	AVERAGE(3),
	PAETH(4);

	companion object {
		val mapping: Map<Int, PNGAdaptiveFilterType> = entries.associateBy(PNGAdaptiveFilterType::code)
	}
}