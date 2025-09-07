package org.bread_experts_group.api.socket

import java.net.SocketAddress

class BSLBluetoothSocketAddress(val address: ByteArray) : SocketAddress() {
	init {
		require(address.size == 6) { "Bluetooth address must be 6 bytes" }
	}

	override fun toString(): String = "BSLBluetoothSocketAddress(${address.joinToString(":") { it.toHexString() }})"
}