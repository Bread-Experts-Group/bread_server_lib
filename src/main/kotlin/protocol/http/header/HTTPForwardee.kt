package org.bread_experts_group.protocol.http.header

import java.net.URI

/**
 * @author Miko Elbrecht
 * @since 3.4.0-selector_nio_exp3
 */
data class HTTPForwardee(
	val by: HTTPForwardeeIdentifier?,
	val `for`: HTTPForwardeeIdentifier?,
	val host: URI?,
	val proto: String?
)