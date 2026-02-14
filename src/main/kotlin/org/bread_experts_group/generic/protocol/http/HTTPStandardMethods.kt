package org.bread_experts_group.generic.protocol.http

import org.bread_experts_group.generic.Mappable

/**
 * HyperText Transfer Protocol (HTTP) methods.
 * @since D1F3N6P0
 * @author Miko Elbrecht
 * @see HTTPMessage
 */
enum class HTTPStandardMethods : Mappable<HTTPStandardMethods, String> {
	/**
	 * Transfer a current representation of the target resource.
	 *
	 * Defined under [Section 9.3.1](https://www.rfc-editor.org/rfc/rfc9110#GET) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	GET,

	/**
	 * Same as GET, but do not transfer the response content.
	 *
	 * Defined under [Section 9.3.2](https://www.rfc-editor.org/rfc/rfc9110#HEAD) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	HEAD,

	/**
	 * Perform resource-specific processing on the request content.
	 *
	 * Defined under [Section 9.3.3](https://www.rfc-editor.org/rfc/rfc9110#POST) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	POST,

	/**
	 * Replace all current representations of the target resource with the request content.
	 *
	 * Defined under [Section 9.3.4](https://www.rfc-editor.org/rfc/rfc9110#PUT) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	PUT,

	/**
	 * Remove all current representations of the target resource.
	 *
	 * Defined under [Section 9.3.5](https://www.rfc-editor.org/rfc/rfc9110#DELETE) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	DELETE,

	/**
	 * Establish a tunnel to the server identified by the target resource.
	 *
	 * Defined under [Section 9.3.6](https://www.rfc-editor.org/rfc/rfc9110#CONNECT) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	CONNECT,

	/**
	 * Describe the communication options for the target resource.
	 *
	 * Defined under [Section 9.3.7](https://www.rfc-editor.org/rfc/rfc9110#OPTIONS) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	OPTIONS,

	/**
	 * Perform a message loop-back test along the path to the target resource.
	 *
	 * Defined under [Section 9.3.8](https://www.rfc-editor.org/rfc/rfc9110#TRACE) of
	 * [IETF RFC 9110](https://www.rfc-editor.org/rfc/rfc9110),
	 * [Section 9](https://www.rfc-editor.org/rfc/rfc9110#section-9).
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	TRACE;

	/**
	 * The identifier used for mapping a raw value to an enum.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	override val id: String = name

	/**
	 * The string used for representing an enum in a textual, formatted way.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	override val tag: String = name

	/**
	 * Converts this enum value into a string.
	 * @author Miko Elbrecht
	 * @since D1F3N6P0
	 */
	override fun toString(): String = name
}