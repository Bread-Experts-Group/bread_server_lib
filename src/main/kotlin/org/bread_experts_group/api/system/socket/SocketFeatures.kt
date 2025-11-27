package org.bread_experts_group.api.system.socket

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.connect.IPv4TCPConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.connect.IPv4TCPConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature

object SocketFeatures {
	val CONNECT_IPV4 = object : FeatureExpression<SocketConnectFeature
	<IPv4TCPConnectionFeatureIdentifier, IPv4TCPConnectionDataIdentifier>> {
		override val name: String = "(IPv4) Connect Socket"
	}
}