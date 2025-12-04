package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.IPv6TCPFeatures

abstract class IPv6TCPSocketFeature : IPv6TCPFeatureImplementation<IPv6TCPSocketFeature>() {
	override val expresses: FeatureExpression<IPv6TCPSocketFeature> = IPv6TCPFeatures.SOCKET
	abstract fun openSocket(): IPv6Socket
}