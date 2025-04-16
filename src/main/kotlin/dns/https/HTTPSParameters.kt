package org.bread_experts_group.dns.https

enum class HTTPSParameters(val code: Int) {
	MANDATORY(0),
	ADDITIONAL_SUPPORTED_PROTOCOLS(1),
	NO_SUPPORT_FOR_DEFAULT_PROTOCOL(2),
	ALTERNATIVE_PORT(3),
	IPV4_HINT(4),
	ENCRYPTED_CLIENT_HELLO(5),
	IPV6_HINT(6);

	companion object {
		val mapping = entries.associateBy(HTTPSParameters::code)
	}
}