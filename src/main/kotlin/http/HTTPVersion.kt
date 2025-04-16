package org.bread_experts_group.http

enum class HTTPVersion(val tag: String) {
	HTTP_1_1("HTTP/1.1"),
	HTTP_2("HTTP/2.0"),
	OTHER("HTTP/???");

	companion object {
		val safeMapping = entries.associateBy(HTTPVersion::name)
	}
}