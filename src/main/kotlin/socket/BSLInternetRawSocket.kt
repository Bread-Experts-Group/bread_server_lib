package org.bread_experts_group.socket

import java.nio.channels.NetworkChannel
import java.util.*

/**
 * A raw socket for network communications based within OSI Layer 3 (network).
 *
 * **NOTE:** This socket may be subject to local operating system limitations, such as stripping off
 * packet headers for IPv6.
 * @author Miko Elbrecht
 * @since 4.0.0
 */
abstract class BSLInternetRawSocket(
	protected val internetType: BSLInternetProtocolSocketType,
	protected val accepts: Array<String>
) : AddressableByteChannel, NetworkChannel {
	companion object {
		fun open(
			internetType: BSLInternetProtocolSocketType
		): BSLInternetRawSocket = ServiceLoader.load(BSLInternetRawSocket::class.java).firstOrNull {
			it.accepts.contains(System.getProperty("os.name")) && it.internetType == internetType
		} ?: throw NoSocketAvailableException(
			"No available socket implementation could be found for [${System.getProperty("os.name")} / $internetType]."
		)
	}

	abstract fun promiscuous(toggle: Boolean)
}