package org.bread_experts_group.api.compile.ebc

enum class EBCMoveTypes(val letter: Char) {
	BITS_8_BYTE('b'),
	BITS_16_WORD('w'),
	BITS_32_DOUBLEWORD('d'),
	BITS_64_QUADWORD('q')
}