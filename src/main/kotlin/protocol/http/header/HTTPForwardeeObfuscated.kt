package org.bread_experts_group.protocol.http.header

/**
 * @author Miko Elbrecht
 * @since 3.4.0-selector_nio_exp3
 */
open class HTTPForwardeeObfuscated(val identifier: String) : HTTPForwardeeIdentifier() {
	override fun toString(): String = "\"${this.identifier}\""
}