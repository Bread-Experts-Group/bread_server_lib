package org.bread_experts_group.protocol.http

enum class HTTPMethod {
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

	companion object {
		val safeMapping: Map<String, HTTPMethod> = entries.associateBy(HTTPMethod::name)
	}
}