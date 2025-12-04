package org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.IPv4Socket
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.IPv4UDPFeatures

abstract class IPv4UDPSocketFeature : IPv4UDPFeatureImplementation<IPv4UDPSocketFeature>() {
	override val expresses: FeatureExpression<IPv4UDPSocketFeature> = IPv4UDPFeatures.SOCKET
	abstract fun openSocket(): IPv4Socket
}