package org.bread_experts_group.protocol.ssh

enum class SSHConnectionState {
	BANNER,
	KEY_EXCHANGE_NEGOTIATION,
	KEY_EXCHANGE
}