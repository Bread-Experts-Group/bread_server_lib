package org.bread_experts_group.protocol.http.h11

/**
 * @param statusCode The result of a previously sent request, and an indicator of the semantics for this response.
 * Defined under [Section 15](https://www.rfc-editor.org/rfc/rfc9110#section-15) of
 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110).
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class ResponseH11StatusCode(
	val statusCode: Int
) : HTTP11ResponseParsingDataIdentifier