package org.bread_experts_group.protocol.http

import org.bread_experts_group.coder.Mappable

enum class HTTPMethod : Mappable<HTTPMethod, String> {
	GET,
	HEAD,
	POST,
	PUT,
	DELETE,
	CONNECT,
	OPTIONS,
	TRACE,
	PATCH,
	SSTP_DUPLEX_POST, // SSTP
	PRI, // HTTP/2 Preface
	OTHER;

	override val id: String = name
	override val tag: String = id
	override fun other(): HTTPMethod? = OTHER
	override fun toString(): String = stringForm()
}