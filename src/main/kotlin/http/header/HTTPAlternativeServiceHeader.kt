package org.bread_experts_group.http.header

data class HTTPAlternativeServiceHeader(val services: List<HTTPAlternativeService>) {
	override fun toString(): String = services.map {
		val builder = StringBuilder()
		builder.append(it.protocol)
		builder.append("=\"")
		builder.append(it.host)
		builder.append(':')
		builder.append(it.port)
		builder.append('"')
		if (it.maxAge != null) builder.append("; ma=${it.maxAge}")
		if (it.persistent) builder.append("; persist=1")
		builder.toString()
	}.joinToString(", ") { it }
}