package org.bread_experts_group.coder.format.id3.frame

import org.bread_experts_group.coder.Flaggable

enum class ID3HeaderFlags(override val position: Long) : Flaggable {
	UNSYNCHRONIZATION(0b10000000),
	EXTENDED_HEADER(0b01000000),
	EXPERIMENTAL(0b00100000)
}