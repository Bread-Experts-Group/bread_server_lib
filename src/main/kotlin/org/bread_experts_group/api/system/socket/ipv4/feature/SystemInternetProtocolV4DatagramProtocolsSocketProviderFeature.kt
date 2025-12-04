@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv4.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv4.SystemInternetProtocolV4SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.datagram.feature.SystemInternetProtocolV4DatagramProtocolFeatureImplementation
import java.util.logging.Logger

abstract class SystemInternetProtocolV4DatagramProtocolsSocketProviderFeature :
	SystemInternetProtocolV4SocketProviderFeatureImplementation
	<SystemInternetProtocolV4DatagramProtocolsSocketProviderFeature>(),
	FeatureProvider<SystemInternetProtocolV4DatagramProtocolFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV4DatagramProtocolsSocketProviderFeature> =
		SystemInternetProtocolV4SocketProviderFeatures.DATAGRAM_PROTOCOLS

	override val logger: Logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemInternetProtocolV4DatagramProtocolFeatureImplementation<*>>,
			MutableList<SystemInternetProtocolV4DatagramProtocolFeatureImplementation<*>>> =
		mutableMapOf()
}