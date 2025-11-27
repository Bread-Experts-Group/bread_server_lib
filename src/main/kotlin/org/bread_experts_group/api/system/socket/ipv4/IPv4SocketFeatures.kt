package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4TCPConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4TCPConnectionFeatureIdentifier

object IPv4SocketFeatures {
	val CONNECT = object : FeatureExpression<SocketConnectFeature
	<IPv4TCPConnectionFeatureIdentifier, IPv4TCPConnectionDataIdentifier>> {
		override val name: String = "(IPv4) Connect Socket"
	}

	val SEND = object : FeatureExpression<
			SocketSendFeature<IPv4TCPConnectionFeatureIdentifier, IPv4TCPConnectionDataIdentifier>> {
		override val name: String = "(IPv4) Socket Send Data"
	}

	val RECEIVE = object : FeatureExpression<
			SocketReceiveFeature<IPv4TCPConnectionFeatureIdentifier, IPv4TCPConnectionDataIdentifier>> {
		override val name: String = "(IPv4) Socket Receive Data"
	}
}