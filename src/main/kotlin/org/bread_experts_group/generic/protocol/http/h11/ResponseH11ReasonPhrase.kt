package org.bread_experts_group.generic.protocol.http.h11

/**
 * @param reasonPhrase A string possibly associated with [org.bread_experts_group.generic.protocol.http.h11.ResponseH11StatusCode].
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class ResponseH11ReasonPhrase(
	val reasonPhrase: String
) : org.bread_experts_group.generic.protocol.http.h11.HTTP11ResponseParsingDataIdentifier