package org.bread_experts_group.api.system.socket.ipv6.posix

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.send.SendSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendFeatureIdentifier
import org.bread_experts_group.api.system.socket.system.DeferredSend
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
	): DeferredOperation<IPv6SendDataIdentifier> =
		object : DeferredSend<IPv6SendDataIdentifier>(monitor) {
			override fun send(): List<IPv6SendDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val address = features.firstNotNullOfOrNull { it as? InternetProtocolV6AddressPortData }
				if (address != null) TODO("SEND TO")
				val dataOut = mutableListOf<IPv6SendDataIdentifier>()
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
				dataOut.add(SendSizeData(sent))
				if (sent == -1L) {
					if (errno == 104) dataOut.add(StandardSocketStatus.CONNECTION_CLOSED)
					else throwLastErrno()
				}
				return dataOut
			}
		}
}