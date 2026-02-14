@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.feature

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.FeatureProvider
import org.bread_experts_group.api.system.socket.ipv6.SystemInternetProtocolV6SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv6.datagram.feature.SystemInternetProtocolV6DatagramProtocolFeatureImplementation

abstract class SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature :
	SystemInternetProtocolV6SocketProviderFeatureImplementation<SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature>(),
	FeatureProvider<SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>> {
	override val expresses: FeatureExpression<SystemInternetProtocolV6DatagramProtocolsSocketProviderFeature> =
		SystemInternetProtocolV6SocketProviderFeatures.DATAGRAM_PROTOCOLS

	override val logger
		get() = TODO("Not yet implemented")
	override val supportedFeatures: MutableMap<
			FeatureExpression<out SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>>,
			MutableList<SystemInternetProtocolV6DatagramProtocolFeatureImplementation<*>>> =
		mutableMapOf()
}