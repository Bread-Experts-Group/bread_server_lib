package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.SystemInternetProtocolV4SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.SystemInternetProtocolV4StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPV4TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.InternetProtocolV4AddressData
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPart
import org.bread_experts_group.api.system.socket.resolution.WindowsResolutionFeatures
import org.bread_experts_group.api.system.socket.resolution_namespace_provider.type.WindowsResolutionNamespaceTypes

fun main() {
	val netSockets = SystemProvider.get(SystemFeatures.NETWORKING_SOCKETS)
	val tcpV4 = netSockets
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V4)
		.get(SystemInternetProtocolV4SocketProviderFeatures.STREAM_PROTOCOLS)
		.get(SystemInternetProtocolV4StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL)
	val dns = netSockets.get(SystemSocketProviderFeatures.RESOLUTION_NAMESPACE_PROVIDERS).first {
		it.type == WindowsResolutionNamespaceTypes.NETWORK_LOCATION_AWARENESS
	}
	val tcpV4Resolution = tcpV4.get(IPV4TCPFeatures.NAME_RESOLUTION)
	val data = tcpV4Resolution.resolve(
		"learn.microsoft.com",
		WindowsResolutionFeatures.CANONICAL_NAME,
		WindowsResolutionFeatures.FULLY_QUALIFIED_DOMAIN_NAME
	)
	println(
		data.firstNotNullOf { it as? ResolutionDataPart }.data.firstNotNullOf { it as? InternetProtocolV4AddressData }
	)
}