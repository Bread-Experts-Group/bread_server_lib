package org.bread_experts_group.api.system.socket.ipv4.stream.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv4.stream.SystemInternetProtocolV4StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.feature.IPV4TCPFeatureImplementation
import java.util.logging.Logger

abstract class SystemInternetProtocolV4TCPFeature :
	SystemInternetProtocolV4StreamProtocolFeatureImplementation<SystemInternetProtocolV4TCPFeature>(),
	FeatureProvider<IPV4TCPFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV4TCPFeature> =
		SystemInternetProtocolV4StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL
	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out IPV4TCPFeatureImplementation<*>>,
			MutableList<IPV4TCPFeatureImplementation<*>>> = mutableMapOf()
}