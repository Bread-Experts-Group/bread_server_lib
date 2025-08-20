package org.bread_experts_group.protocol.old.http

import org.bread_experts_group.coder.Mappable

enum class HTTPVersion(
	override val id: String
) : Mappable<HTTPVersion, String> {
	HTTP_0_9("HTTP/0.9"),
	HTTP_1_0("HTTP/1.0"),
	HTTP_1_1("HTTP/1.1"),
	HTTP_2("HTTP/2"),
	HTTP_3("HTTP/3");

	override val tag: String = id
	override fun toString(): String = stringForm()
}