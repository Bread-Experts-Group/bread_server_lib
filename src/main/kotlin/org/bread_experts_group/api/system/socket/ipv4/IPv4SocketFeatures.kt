package org.bread_experts_group.api.system.socket.ipv4

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.feature.*
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.bind.IPv4BindDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.bind.IPv4BindFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.config.IPv4ConfigureDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.config.IPv4ConfigureFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.connect.IPv4ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.listen.IPv4ListenDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.listen.IPv4ListenFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendFeatureIdentifier

object IPv4SocketFeatures {
	val CONNECT = object : FeatureExpression<SocketConnectFeature
	<IPv4ConnectionFeatureIdentifier, IPv4ConnectionDataIdentifier>> {
		override val name: String = "(IPv4) Connect Socket"
	}

	val SEND = object : FeatureExpression<SocketSendFeature<IPv4SendFeatureIdentifier, IPv4SendDataIdentifier>> {
		override val name: String = "(IPv4) Socket Send Data"
	}

	val RECEIVE = object : FeatureExpression<SocketReceiveFeature
	<IPv4ReceiveFeatureIdentifier, IPv4ReceiveDataIdentifier>> {
		override val name: String = "(IPv4) Socket Receive Data"
	}

	val BIND = object : FeatureExpression<SocketBindFeature<IPv4BindFeatureIdentifier, IPv4BindDataIdentifier>> {
		override val name: String = "(IPv4) Bind Socket"
	}

	val LISTEN = object : FeatureExpression<SocketListenFeature
	<IPv4ListenFeatureIdentifier, IPv4ListenDataIdentifier>> {
		override val name: String = "(IPv4) Listen on Socket"
	}

	val ACCEPT = object : FeatureExpression<SocketAcceptFeature
	<IPv4AcceptFeatureIdentifier, IPv4AcceptDataIdentifier>> {
		override val name: String = "(IPv4) Accept Connecting Socket"
	}

	val CONFIGURE = object : FeatureExpression<SocketConfigureFeature
	<IPv4ConfigureFeatureIdentifier, IPv4ConfigureDataIdentifier>> {
		override val name: String = "(IPv6) Configure Socket"
	}
}