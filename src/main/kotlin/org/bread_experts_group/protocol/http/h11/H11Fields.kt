package org.bread_experts_group.protocol.http.h11

/**
 * @param fields The request headers. Defined under
 * [Section 5](https://datatracker.ietf.org/doc/html/rfc9112#name-field-syntax) of
 * [IETF RFC 9112](https://www.rfc-editor.org/rfc/rfc9112).
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class H11Fields(
	val fields: Map<String, String>
) : HTTP11RequestParsingDataIdentifier, HTTP11ResponseParsingDataIdentifier