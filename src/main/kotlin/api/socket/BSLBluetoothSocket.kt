package org.bread_experts_group.api.socket

import java.util.*

abstract class BSLBluetoothSocket(accepts: Array<String>) : BSLSocket(accepts) {
	companion object {
		fun open(): BSLBluetoothSocket = ServiceLoader.load(BSLBluetoothSocket::class.java).firstOrNull {
			it.accepts.contains(System.getProperty("os.name"))
		} ?: throw NoSocketAvailableException(
			"No available socket implementation could be found for [${System.getProperty("os.name")}]."
		)
	}
}