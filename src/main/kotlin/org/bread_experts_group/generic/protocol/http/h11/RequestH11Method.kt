package org.bread_experts_group.generic.protocol.http.h11

import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.generic.protocol.http.HTTPStandardMethods

/**
 * @param method The request method to be performed on the [org.bread_experts_group.generic.protocol.http.h11.RequestH11Target], case-sensitive. Defined under
 * [Section 3.1](https://datatracker.ietf.org/doc/html/rfc9112#name-method) of
 * [IETF RFC 9112](https://www.rfc-editor.org/rfc/rfc9112).
 * @author Miko Elbrecht
 * @since D1F3N6P0
 */
data class RequestH11Method(
	val method: MappedEnumeration<String, HTTPStandardMethods>
) : org.bread_experts_group.generic.protocol.http.h11.HTTP11RequestParsingDataIdentifier