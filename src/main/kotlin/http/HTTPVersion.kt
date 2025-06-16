package org.bread_experts_group.http

enum class HTTPVersion(val tag: String) {
	HTTP_0_9("HTTP/0.9"),
	HTTP_1_0("HTTP/1.0"),
	HTTP_1_1("HTTP/1.1"),
	HTTP_2("HTTP/2"),
	HTTP_3("HTTP/3");

	companion object {
		val mapping: Map<String, HTTPVersion> = entries.associateBy(HTTPVersion::tag)
	}
}