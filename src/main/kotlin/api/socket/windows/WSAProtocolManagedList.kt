package org.bread_experts_group.api.socket.windows

import java.lang.foreign.Arena

data class WSAProtocolManagedList(
	val list: List<WSAProtocol>,
	val memory: Arena
)