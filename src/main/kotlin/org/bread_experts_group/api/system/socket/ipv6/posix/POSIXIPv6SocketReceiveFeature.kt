package org.bread_experts_group.api.system.socket.ipv6.posix

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.system.DeferredSocketReceive
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.posix.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class POSIXIPv6SocketReceiveFeature(
	private val socket: Int,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>>
) : SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6ReceiveFeatureIdentifier
	): DeferredSocketOperation<IPv6ReceiveDataIdentifier> =
		object : DeferredSocketReceive<IPv6ReceiveDataIdentifier>(monitor) {
			override fun receive(): List<IPv6ReceiveDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val dataOut = mutableListOf<IPv6ReceiveDataIdentifier>()
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
				val received = nativeRecvMsg!!.invokeExact(
					capturedStateSegment,
					socket,
					message,
					0
				) as Long
				dataOut.add(ReceiveSizeData(received))
				if (received == -1L) {
					if (errno == 104) dataOut.add(StandardSocketStatus.CONNECTION_CLOSED)
					else throwLastErrno()
				}
				return dataOut
			}
		}
}