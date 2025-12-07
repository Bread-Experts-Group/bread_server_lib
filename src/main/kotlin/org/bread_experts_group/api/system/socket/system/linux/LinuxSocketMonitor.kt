package org.bread_experts_group.api.system.socket.system.linux

import org.bread_experts_group.api.system.socket.system.SocketMonitor

class LinuxSocketMonitor(private val socket: Int) : SocketMonitor() {
	var forAccept = false
	fun wakeup() = LinuxSocketEventManager.wakeupSocket(socket)
}