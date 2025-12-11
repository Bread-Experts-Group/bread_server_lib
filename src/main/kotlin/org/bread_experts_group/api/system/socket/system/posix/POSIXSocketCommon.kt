package org.bread_experts_group.api.system.socket.system.posix

import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.close.SocketCloseDataIdentifier
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.close.StandardCloseFeatures
import org.bread_experts_group.api.system.socket.system.linux.LinuxSocketEventManager.dropSocket
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.errno
import org.bread_experts_group.ffi.posix.nativeClose
import org.bread_experts_group.ffi.posix.nativeShutdown
import org.bread_experts_group.ffi.posix.throwLastErrno

fun posixClose(
	socket: Int,
	vararg features: SocketCloseFeatureIdentifier
): List<SocketCloseDataIdentifier> {
	val supportedFeatures = mutableListOf<SocketCloseDataIdentifier>()
	val stopTx = features.contains(StandardCloseFeatures.STOP_TX)
	val stopRx = features.contains(StandardCloseFeatures.STOP_RX)
	if (stopTx || stopRx) {
		val status = nativeShutdown!!.invokeExact(
			capturedStateSegment,
			socket,
			if (stopTx && stopRx) {
				supportedFeatures.add(StandardCloseFeatures.STOP_TX)
				supportedFeatures.add(StandardCloseFeatures.STOP_RX)
				2
			} else if (stopTx) {
				supportedFeatures.add(StandardCloseFeatures.STOP_TX)
				1
			} else {
				supportedFeatures.add(StandardCloseFeatures.STOP_RX)
				0
			}
		) as Int
		if (status != 0) {
			if (errno == 107) supportedFeatures.add(StandardSocketStatus.CONNECTION_CLOSED)
			else throwLastErrno()
		}
	}
	if (features.contains(StandardCloseFeatures.RELEASE)) {
		supportedFeatures.add(StandardCloseFeatures.RELEASE)
		val status = nativeClose!!.invokeExact(
			capturedStateSegment,
			socket
		) as Int
		if (status != 0) throwLastErrno()
		dropSocket(socket)
	}
	return supportedFeatures
}