package org.bread_experts_group.api.system.socket.ipv6.posix

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendFeatureIdentifier
import org.bread_experts_group.api.system.socket.send.SendSizeData
import org.bread_experts_group.api.system.socket.system.DeferredSocketSend
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class POSIXIPv6SocketSendToFeature(
	private val socket: Int,
	private val monitor: SocketMonitor,
	private val checkForAddress: Boolean,
	expresses: FeatureExpression<SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>>
) : SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6SendFeatureIdentifier
	): DeferredSocketOperation<IPv6SendDataIdentifier> =
		object : DeferredSocketSend<IPv6SendDataIdentifier>(monitor) {
			override fun send(): List<IPv6SendDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val message = tempArena.allocate(msghdr)
				val iov = tempArena.allocate(iovec, data.size.toLong())
				msghdr_msg_iov.set(message, 0L, iov)
				msghdr_msg_iovlen.set(message, 0L, data.size.toLong())
				var offset = 0L
				data.forEach {
					iovec_iov_base.set(iov, offset, it)
					iovec_iov_len.set(iov, offset, it.byteSize())
					offset += iovec.byteSize()
				}
				val sent = nativeSendMsg!!.invokeExact(
					capturedStateSegment,
					socket,
					message,
					0
				) as Long
				if (sent == -1L) throwLastErrno()
				return listOf(SendSizeData(sent))
			}
		}
}