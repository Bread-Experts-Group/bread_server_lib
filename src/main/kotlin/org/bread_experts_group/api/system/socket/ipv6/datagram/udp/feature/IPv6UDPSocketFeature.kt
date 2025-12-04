package org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.IPv6UDPFeatures

abstract class IPv6UDPSocketFeature : IPv6UDPFeatureImplementation<IPv6UDPSocketFeature>() {
	override val expresses: FeatureExpression<IPv6UDPSocketFeature> = IPv6UDPFeatures.SOCKET
	abstract fun openSocket(): IPv6Socket
}