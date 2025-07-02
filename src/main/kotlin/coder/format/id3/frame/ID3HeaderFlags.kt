package org.bread_experts_group.coder.format.id3.frame

import org.bread_experts_group.coder.Flaggable

enum class ID3HeaderFlags(override val position: Long) : Flaggable {
	RESERVED_7(0b10000000),
	RESERVED_6(0b01000000),
	RESERVED_5(0b00100000),
	RESERVED_4(0b00010000),
	RESERVED_3(0b00001000),
	RESERVED_2(0b00000100),
	RESERVED_1(0b00000010),
	RESERVED_0(0b00000001)
}