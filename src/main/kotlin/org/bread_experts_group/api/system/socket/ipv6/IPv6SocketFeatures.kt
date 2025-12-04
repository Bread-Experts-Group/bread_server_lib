package org.bread_experts_group.api.system.socket.ipv6

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.system.socket.feature.*
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.bind.IPv6BindDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.bind.IPv6BindFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.config.IPv6ConfigureDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.config.IPv6ConfigureFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.listen.IPv6ListenDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.listen.IPv6ListenFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendFeatureIdentifier

object IPv6SocketFeatures {
	val CONNECT = object : FeatureExpression<SocketConnectFeature
	<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>> {
		override val name: String = "(IPv6) Connect Socket"
	}

	val SEND = object : FeatureExpression<SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>> {
		override val name: String = "(IPv6) Socket Send Data"
	}

	val RECEIVE = object : FeatureExpression<SocketReceiveFeature
	<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>> {
		override val name: String = "(IPv6) Socket Receive Data"
	}

	val BIND = object : FeatureExpression<SocketBindFeature<IPv6BindFeatureIdentifier, IPv6BindDataIdentifier>> {
		override val name: String = "(IPv6) Bind Socket"
	}

	val LISTEN = object : FeatureExpression<SocketListenFeature
	<IPv6ListenFeatureIdentifier, IPv6ListenDataIdentifier>> {
		override val name: String = "(IPv6) Listen on Socket"
	}

	val ACCEPT = object : FeatureExpression<SocketAcceptFeature
	<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>> {
		override val name: String = "(IPv6) Accept Connecting Socket"
	}

	val CONFIGURE = object : FeatureExpression<SocketConfigureFeature
	<IPv6ConfigureFeatureIdentifier, IPv6ConfigureDataIdentifier>> {
		override val name: String = "(IPv6) Configure Socket"
	}
}