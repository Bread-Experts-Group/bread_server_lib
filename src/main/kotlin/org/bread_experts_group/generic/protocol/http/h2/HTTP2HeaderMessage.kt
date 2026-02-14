package org.bread_experts_group.generic.protocol.http.h2

data class HTTP2HeaderMessage(
	val headers: Map<String, List<String>>,
	val noData: Boolean,
	val stream: Int
)