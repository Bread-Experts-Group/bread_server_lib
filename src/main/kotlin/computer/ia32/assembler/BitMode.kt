package org.bread_experts_group.computer.ia32.assembler

enum class BitMode(val bits: Int) {
	BITS_8(8),
	BITS_16(16),
	BITS_32(32),
	BITS_64(64);

	companion object {
		val mapping: Map<Int, BitMode> = entries.associateBy { it.bits }
	}
}