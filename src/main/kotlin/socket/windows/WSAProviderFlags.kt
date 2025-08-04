package org.bread_experts_group.socket.windows

import org.bread_experts_group.coder.Flaggable

enum class WSAProviderFlags(override val position: Long) : Flaggable {
	PFL_MULTIPLE_PROTO_ENTRIES(0x00000001),
	PFL_RECOMMENDED_PROTO_ENTRY(0x00000002),
	PFL_HIDDEN(0x00000004),
	PFL_MATCHES_PROTOCOL_ZERO(0x00000008),
	PFL_NETWORKDIRECT_PROVIDER(0x00000010)
}