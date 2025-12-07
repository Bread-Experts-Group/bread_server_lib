package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.feature.SocketListenFeature
import org.bread_experts_group.api.system.socket.ipv6.listen.IPv6ListenDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.listen.IPv6ListenFeatureIdentifier
import org.bread_experts_group.api.system.socket.listen.ListenBacklogFeature
import org.bread_experts_group.api.system.socket.system.windows.SOMAXCONN
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.nativeListen

class WindowsIPv6SocketListenFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketListenFeature<IPv6ListenFeatureIdentifier, IPv6ListenDataIdentifier>>
) : SocketListenFeature<IPv6ListenFeatureIdentifier, IPv6ListenDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun listen(vararg features: IPv6ListenFeatureIdentifier): List<IPv6ListenDataIdentifier> {
		val supportedFeatures = mutableListOf<IPv6ListenDataIdentifier>()
		val listenBL = features.firstNotNullOfOrNull { it as? ListenBacklogFeature }
		val backlog = if (listenBL == null) SOMAXCONN
		else {
			supportedFeatures.add(listenBL)
			listenBL.backlog
		}
		val status = nativeListen!!.invokeExact(
			capturedStateSegment,
			socket,
			backlog
		) as Int
		if (status != 0) throwLastWSAError()
		return supportedFeatures
	}
}