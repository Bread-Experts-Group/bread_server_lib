package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.send.IPv4SendFeatureIdentifier
import java.lang.foreign.MemorySegment

class WindowsIPv4SocketSendToFeature(
	private val socket: Long,
	private val checkForAddress: Boolean,
	expresses: FeatureExpression<SocketSendFeature<IPv4SendFeatureIdentifier, IPv4SendDataIdentifier>>
) : SocketSendFeature<IPv4SendFeatureIdentifier, IPv4SendDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv4SendFeatureIdentifier
	): DeferredOperation<IPv4SendDataIdentifier> = TODO("IPv4 re-evaluation $socket $checkForAddress")
}