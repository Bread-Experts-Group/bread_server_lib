package org.bread_experts_group.protocol.http.h2

import org.bread_experts_group.protocol.http.HTTPMethod
import org.bread_experts_group.protocol.http.HTTPRequest
import java.io.InputStream
import java.net.URI

class HTTP2Request internal constructor(
	val stream: Int,
	method: HTTPMethod,
	path: URI,
	headers: Map<String, String> = emptyMap(),
	data: InputStream
) : HTTPRequest(method, path, headers, TODO("HTTP/2 data"))