package org.bread_experts_group.api.system.socket.ipv6.datagram.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv6.datagram.SystemInternetProtocolV6DatagramProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv6.datagram.udp.feature.IPv6UDPFeatureImplementation

abstract class SystemInternetProtocolV6UDPFeature :
	SystemInternetProtocolV6DatagramProtocolFeatureImplementation<SystemInternetProtocolV6UDPFeature>(),
	FeatureProvider<IPv6UDPFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV6UDPFeature> =
		SystemInternetProtocolV6DatagramProtocolFeatures.USER_DATAGRAM_PROTOCOL
	override val logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IPv6UDPFeatureImplementation<*>>,
			MutableList<IPv6UDPFeatureImplementation<*>>> = mutableMapOf()
}