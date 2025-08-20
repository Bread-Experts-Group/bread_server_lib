package org.bread_experts_group.protocol.old.http.header

data class HTTPAlternativeService(
	val protocol: String,
	val host: String = "",
	val port: UShort,
	val maxAge: Int? = null,
	val persistent: Boolean = false
)