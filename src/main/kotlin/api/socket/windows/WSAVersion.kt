package org.bread_experts_group.api.socket.windows

data class WSAVersion(
	val major: UByte,
	val minor: UByte
) {
	constructor(word: UShort) : this(
		(word.toUInt() shr 8).toUByte(),
		(word.toUInt() and 0xFFu).toUByte(),
	)
}