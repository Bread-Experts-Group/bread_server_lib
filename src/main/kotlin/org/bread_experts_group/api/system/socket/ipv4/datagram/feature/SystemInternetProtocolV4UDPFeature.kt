package org.bread_experts_group.api.system.socket.ipv4.datagram.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv4.datagram.SystemInternetProtocolV4DatagramProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv4.datagram.udp.feature.IPv4UDPFeatureImplementation
import java.util.logging.Logger

abstract class SystemInternetProtocolV4UDPFeature :
	SystemInternetProtocolV4DatagramProtocolFeatureImplementation<SystemInternetProtocolV4UDPFeature>(),
	FeatureProvider<IPv4UDPFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV4UDPFeature> =
		SystemInternetProtocolV4DatagramProtocolFeatures.USER_DATAGRAM_PROTOCOL
	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IPv4UDPFeatureImplementation<*>>,
			MutableList<IPv4UDPFeatureImplementation<*>>> = mutableMapOf()
}