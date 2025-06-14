package org.bread_experts_group.coder.format.png

enum class PNGCompressionType(val code: Int) {
	DEFLATE(0);

	companion object {
		val mapping: Map<Int, PNGCompressionType> = entries.associateBy(PNGCompressionType::code)
	}
}