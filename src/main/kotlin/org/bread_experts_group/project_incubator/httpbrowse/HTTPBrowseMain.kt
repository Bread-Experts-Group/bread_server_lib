package org.bread_experts_group.project_incubator.httpbrowse

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.socket.SystemSocketProviderFeatures
import org.bread_experts_group.api.system.socket.close.StandardCloseFeatures
import org.bread_experts_group.api.system.socket.ipv6.*
import org.bread_experts_group.api.system.socket.ipv6.config.WindowsIPv6SocketConfigurationFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.SystemInternetProtocolV6StreamProtocolFeatures
import org.bread_experts_group.api.system.socket.ipv6.stream.tcp.IPv6TCPFeatures
import org.bread_experts_group.api.system.socket.resolution.ResolutionDataPart
import org.bread_experts_group.generic.io.reader.BSLReader
import org.bread_experts_group.generic.io.reader.BSLReader.Companion.socketReadCheck
import org.bread_experts_group.generic.io.reader.BSLWriter
import org.bread_experts_group.generic.io.reader.BSLWriter.Companion.socketWriteCheck
import org.bread_experts_group.generic.protocol.http.h11.HTTP11ParsingStatus
import org.bread_experts_group.generic.protocol.http.h11.RequestH11Method
import org.bread_experts_group.generic.protocol.http.h11.RequestH11Target
import org.bread_experts_group.generic.protocol.http.h11.h11RequestFrom
import org.bread_experts_group.generic.protocol.http.h2.HTTP2ConnectionManager

fun main() {
	val ipv6 = SystemProvider.get(SystemFeatures.NETWORKING_SOCKETS)
		.get(SystemSocketProviderFeatures.INTERNET_PROTOCOL_V6)
	val v6TCP = ipv6.get(SystemInternetProtocolV6SocketProviderFeatures.STREAM_PROTOCOLS)
		.get(SystemInternetProtocolV6StreamProtocolFeatures.TRANSMISSION_CONTROL_PROTOCOL)
	val v6TCPResolve = v6TCP.get(IPv6TCPFeatures.NAME_RESOLUTION)
	val resolveData = v6TCPResolve.resolve(
		"google.com", 80u
	).firstNotNullOf { it as? ResolutionDataPart }.data.firstNotNullOf { it as? InternetProtocolV6AddressData }
	val v6Sockets = v6TCP.get(IPv6TCPFeatures.SOCKET)
	val v6Socket = v6Sockets.openSocket()
	v6Socket.get(IPv6SocketFeatures.CONFIGURE).configure(
		WindowsIPv6SocketConfigurationFeatures.ALLOW_IPV6_AND_IPV4
	)
	v6Socket.get(IPv6SocketFeatures.BIND).bind(
		InternetProtocolV6AddressPortData(
			byteArrayOf(
				0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0
			),
			80u
		)
	)
	v6Socket.get(IPv6SocketFeatures.LISTEN).listen()
	while (true) {
		val nextData = v6Socket.get(IPv6SocketFeatures.ACCEPT).accept().block()
		val next = nextData.firstNotNullOf { it as? IPv6Socket }
		Thread.ofPlatform().start {
			val reader = BSLReader(next.get(IPv6SocketFeatures.RECEIVE), socketReadCheck)
			val writer = BSLWriter(next.get(IPv6SocketFeatures.SEND), socketWriteCheck)
			val h11Request = h11RequestFrom(reader)
			val h11Fail = h11Request.firstNotNullOfOrNull { it as? HTTP11ParsingStatus.BadForm.Version.HTTP11 }
			if (h11Fail == null) {
				println("H11: $h11Request")
				writer.write("HTTP/1.1 200 OK\r\nconnection:close\r\ncontent-length:0\r\n\r\n".toByteArray(Charsets.UTF_8))
				writer.flush()
				next.close(
					StandardCloseFeatures.STOP_RX, StandardCloseFeatures.STOP_TX,
					StandardCloseFeatures.RELEASE
				)
				return@start
			}
			if (h11Fail.was.contentEquals("2.0")) {
				if (h11Request.firstNotNullOf { it as? RequestH11Method }.method.raw != "PRI") {
					println("Not PRI: $h11Request")
					return@start
				}
				if (h11Request.firstNotNullOf { it as? RequestH11Target }.target != "*") {
					println("Not *: $h11Request")
					return@start
				}
				val h2 = HTTP2ConnectionManager.create(reader, writer) as HTTP2ConnectionManager
				h2.logger.label = nextData.toString()
//				println("$h2: $nextData")
				val request = h2.nextStreamHeaders()
				val data = h2.startGetStreamData(request.stream)
//				println("\"${reader.readN(data).toString(Charsets.UTF_8)}\"")
				h2.endGetStreamData(request.stream)
//				println(request.headers)
				h2.sendHeaders(
					request.stream,
					mapOf(
						":status" to "200"
					),
					false
				)
				h2.sendData(request.stream, "hello".toByteArray(Charsets.UTF_8), true)
				return@start
			}
			println("?: $h11Request")
		}
	}
}