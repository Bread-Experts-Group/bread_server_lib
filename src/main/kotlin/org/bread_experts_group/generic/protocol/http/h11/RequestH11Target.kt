package org.bread_experts_group.generic.protocol.http.h11

/**
 * @param target The request target on which to perform [org.bread_experts_group.generic.protocol.http.h11.RequestH11Method] on. Defined under
 * [Section 3.2](https://datatracker.ietf.org/doc/html/rfc9112#name-request-target) of
 * [IETF RFC 9112](https://www.rfc-editor.org/rfc/rfc9112).
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class RequestH11Target(
	val target: String // TODO: Formally define
) : org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingDataIdentifier