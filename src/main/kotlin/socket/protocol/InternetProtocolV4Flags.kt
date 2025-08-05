package org.bread_experts_group.socket.protocol

import org.bread_experts_group.coder.Flaggable

enum class InternetProtocolV4Flags(override val position: Long) : Flaggable {
	RESERVED(0b100),
	DONT_FRAGMENT(0b010),
	MORE_FRAGMENTS(0b001)
}