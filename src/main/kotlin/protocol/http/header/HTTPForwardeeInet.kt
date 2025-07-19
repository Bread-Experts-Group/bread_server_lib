package org.bread_experts_group.protocol.http.header

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetSocketAddress

/**
 * @author Miko Elbrecht
 * @since 3.4.0-selector_nio_exp3
 */
data class HTTPForwardeeInet(val inet: InetSocketAddress) : HTTPForwardeeIdentifier() {
	override fun toString(): String = when (val ip = inet.address) {
		is Inet4Address -> ip.hostAddress + (if (inet.port != 0) ":${inet.port}" else "")
		is Inet6Address -> "\"[${ip.hostAddress}]${if (inet.port != 0) ":${inet.port}" else ""}\""
	}
}