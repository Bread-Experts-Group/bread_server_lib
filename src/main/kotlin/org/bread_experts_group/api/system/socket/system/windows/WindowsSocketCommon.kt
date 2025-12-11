package org.bread_experts_group.api.system.socket.system.windows

import org.bread_experts_group.api.system.socket.close.SocketCloseDataIdentifier
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.close.StandardCloseFeatures
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager.dropSocket
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.nativeCloseSocket
import org.bread_experts_group.ffi.windows.wsa.nativeShutdown

const val SOL_SOCKET = 0xFFFF

const val SO_UPDATE_CONNECT_CONTEXT = 0x7010

const val SOMAXCONN = 0x7FFFFFFF

fun winClose(
	socket: Long,
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
		if (status != 0) throwLastWSAError()
	}
	if (features.contains(StandardCloseFeatures.RELEASE)) {
		supportedFeatures.add(StandardCloseFeatures.RELEASE)
		val status = nativeCloseSocket!!.invokeExact(
			capturedStateSegment,
			socket
		) as Int
		if (status != 0) throwLastWSAError()
		dropSocket(socket)
	}
	return supportedFeatures
}