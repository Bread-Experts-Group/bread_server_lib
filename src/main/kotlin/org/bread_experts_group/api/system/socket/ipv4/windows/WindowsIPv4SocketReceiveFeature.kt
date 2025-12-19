package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveFeatureIdentifier
import java.lang.foreign.MemorySegment

// TODO: MERGE WITH UDP RECEIVE
class WindowsIPv4SocketReceiveFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketReceiveFeature<IPv4ReceiveFeatureIdentifier, IPv4ReceiveDataIdentifier>>
) : SocketReceiveFeature<IPv4ReceiveFeatureIdentifier, IPv4ReceiveDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv4ReceiveFeatureIdentifier
	): DeferredOperation<IPv4ReceiveDataIdentifier> = TODO("IPv4 re-evaluation $socket")
}