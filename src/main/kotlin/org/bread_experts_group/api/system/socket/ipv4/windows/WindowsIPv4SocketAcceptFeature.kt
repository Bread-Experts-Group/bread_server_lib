package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.feature.SocketAcceptFeature
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.accept.IPv4AcceptFeatureIdentifier

class WindowsIPv4SocketAcceptFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketAcceptFeature<IPv4AcceptFeatureIdentifier, IPv4AcceptDataIdentifier>>
) : SocketAcceptFeature<IPv4AcceptFeatureIdentifier, IPv4AcceptDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun accept(
		vararg features: IPv4AcceptFeatureIdentifier
	): DeferredOperation<IPv4AcceptDataIdentifier> = TODO("IPv4 re-evaluation $socket")
}