package bread_experts_group.http

enum class HTTPMethod {
	GET,
	HEAD,
	POST,
	PUT,
	DELETE,
	CONNECT,
	OPTIONS,
	TRACE,
	PATCH;

	companion object {
		val keys = entries.associateBy { it.name }
	}
}