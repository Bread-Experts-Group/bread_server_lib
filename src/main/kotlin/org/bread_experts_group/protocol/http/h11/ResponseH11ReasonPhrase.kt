package org.bread_experts_group.protocol.http.h11

/**
 * @param reasonPhrase A string possibly associated with [ResponseH11StatusCode].
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class ResponseH11ReasonPhrase(
	val reasonPhrase: String
) : HTTP11ResponseParsingDataIdentifier