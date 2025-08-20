package org.bread_experts_group.protocol.old.http.header

import java.net.InetSocketAddress

/**
 * @author Miko Elbrecht
 * @since 3.4.0-selector_nio_exp3
 */
sealed class HTTPForwardeeIdentifier {
	companion object {
		fun parse(value: String): HTTPForwardeeIdentifier = value
			.removeSurrounding("\"")
			.let { unwrapped ->
				val (host, port) = when {
					unwrapped.startsWith('[') -> {
						val inside = unwrapped.substring(1).substringBefore("]")
						inside to unwrapped.substringAfter("]:", "0")
					}

					else -> unwrapped.substringBefore(":") to unwrapped.substringAfter(":", "0")
				}
				if (host.contains('.') || host.contains(':'))
					HTTPForwardeeInet(InetSocketAddress(host, port.toIntOrNull() ?: 0))
				else HTTPForwardeeObfuscated(unwrapped)
			}
	}
}