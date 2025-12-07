package org.bread_experts_group.api.system.socket.system.linux

import org.bread_experts_group.api.system.socket.close.SocketCloseDataIdentifier
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.posix.posixClose

fun linuxClose(
	socket: Int,
	vararg features: SocketCloseFeatureIdentifier
): List<SocketCloseDataIdentifier> {
	LinuxSocketEventManager.dropSocket(socket)
	return posixClose(socket, *features)
}