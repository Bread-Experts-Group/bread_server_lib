package org.bread_experts_group.socket

import java.util.*

/**
 * A socket for network communications based within OSI Layer 4 (transport) using the Transmission Control Protocol
 * (TCP).
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class BSLInternetTCPSocket(
	protected val internetType: BSLInternetProtocolSocketType,
	accepts: Array<String>
) : BSLSocket(accepts) {
	companion object {
		fun open(
			internetType: BSLInternetProtocolSocketType
		): BSLInternetTCPSocket = ServiceLoader.load(BSLInternetTCPSocket::class.java).firstOrNull {
			it.accepts.contains(System.getProperty("os.name")) && it.internetType == internetType
		} ?: throw NoSocketAvailableException(
			"No available socket implementation could be found for [${System.getProperty("os.name")} / $internetType]."
		)
	}
}