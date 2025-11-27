package org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.Socket
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressData
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPV4TCPFeatures

abstract class IPV4TCPSocketFeature : IPV4TCPFeatureImplementation<IPV4TCPSocketFeature>() {
	override val expresses: FeatureExpression<IPV4TCPSocketFeature> = IPV4TCPFeatures.SOCKET
	abstract fun openSocket(): Socket<InternetProtocolV4AddressData>
}