package org.bread_experts_group.api.system.socket.system

import org.bread_experts_group.api.system.BinarySemaphore

open class SocketMonitor {
	val read = BinarySemaphore()
	val write = BinarySemaphore()
	val connect = BinarySemaphore()
	val close = BinarySemaphore()
	val accept = BinarySemaphore()
}