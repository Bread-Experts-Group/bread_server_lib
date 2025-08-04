package org.bread_experts_group.protocol.http.header

data class HTTPAlternativeServiceHeader(val services: List<HTTPAlternativeService>) {
	override fun toString(): String = services.map {
		"${it.protocol}=\"${it.host}:${it.port}\"" +
				"${if (it.maxAge != null) ";ma=${it.maxAge}" else ""}${if (it.persistent) ";persist=1" else ""}"
	}.joinToString(", ") { it }
}