package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionFeatureIdentifier

class WindowsIPv4UDPConnectFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketConnectFeature<IPv4ConnectionFeatureIdentifier, IPv4ConnectionDataIdentifier>>
) : SocketConnectFeature<IPv4ConnectionFeatureIdentifier, IPv4ConnectionDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun connect(
		vararg features: IPv4ConnectionFeatureIdentifier
	): DeferredOperation<IPv4ConnectionDataIdentifier> = TODO("IPv4 re-evaluation $socket")
}