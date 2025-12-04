package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.ipv4.IPv4Socket
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPv4TCPFeatures

abstract class IPv4TCPSocketFeature : IPv4TCPFeatureImplementation<IPv4TCPSocketFeature>() {
	override val expresses: FeatureExpression<IPv4TCPSocketFeature> = IPv4TCPFeatures.SOCKET
	abstract fun openSocket(): IPv4Socket
}