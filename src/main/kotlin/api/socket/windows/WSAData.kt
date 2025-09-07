package org.bread_experts_group.api.socket.windows

data class WSAData(
	val expect: WSAVersion,
	val highest: WSAVersion
)