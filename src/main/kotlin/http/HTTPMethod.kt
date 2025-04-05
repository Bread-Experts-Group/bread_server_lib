package bread_experts_group.http

enum class HTTPMethod {
	GET,
	PUT;

	companion object {
		val keys = entries.associateBy { it.name }
	}
}