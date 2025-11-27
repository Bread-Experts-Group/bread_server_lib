package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.SocketFeatures
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressData
import org.bread_experts_group.api.system.socket.ipv4.InternetProtocolV4AddressPortData
import org.bread_experts_group.api.system.socket.ipv4.SystemInternetProtocolV4SocketProviderFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.SystemInternetProtocolV4StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv4.stream.tcp.IPV4TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPart

fun main() {
	val netSockets = SystemProvider.get(SystemFeatures.NETWORKING_SOCKETS)
	val tcpV4 = netSockets
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V4)
		.get(SystemInternetProtocolV4SocketProviderFeatures.STREAM_PROTOCOLS)
		.get(SystemInternetProtocolV4StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL)
	val tcpV4Resolution = tcpV4.get(IPV4TCPFeatures.NAME_RESOLUTION)
	val resolveData = tcpV4Resolution.resolve("cia.gov")
	val addresses = resolveData
		.mapNotNull { it as? ResolutionDataPart }
		.flatMap { it.data.mapNotNull { a -> a as? InternetProtocolV4AddressData } }
	val tcpV4Sockets = tcpV4.get(IPV4TCPFeatures.SOCKET)
	val tcpV4Socket = tcpV4Sockets.openSocket()
	println("Conn. ${addresses}")
	val connectData = tcpV4Socket.get(SocketFeatures.CONNECT_IPV4).connect(
		*addresses.map { InternetProtocolV4AddressPortData(it.data, 80u) }.toTypedArray()
	)
	println(connectData)
}