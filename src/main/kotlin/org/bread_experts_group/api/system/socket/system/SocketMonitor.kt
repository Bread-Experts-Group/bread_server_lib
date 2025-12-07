package org.bread_experts_group.api.system.socket.system

import java.util.concurrent.Semaphore

open class SocketMonitor {
	val read = Semaphore(0)
	val write = Semaphore(0)
	val connect = Semaphore(0)
	val close = Semaphore(0)
	val accept = Semaphore(0)
}