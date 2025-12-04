package org.bread_experts_group.api.system.socket.ipv6.stream.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv6.stream.SystemInternetProtocolV6StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.IPv6TCPFeatureImplementation
import java.util.logging.Logger

abstract class SystemInternetProtocolV6TCPFeature :
	SystemInternetProtocolV6StreamProtocolFeatureImplementation<SystemInternetProtocolV6TCPFeature>(),
	FeatureProvider<IPv6TCPFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV6TCPFeature> =
		SystemInternetProtocolV6StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL
	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IPv6TCPFeatureImplementation<*>>,
			MutableList<IPv6TCPFeatureImplementation<*>>> = mutableMapOf()
}