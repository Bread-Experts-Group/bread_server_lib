package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.feature.SocketSendFeature
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.send.IPv6SendFeatureIdentifier
import org.bread_experts_group.api.system.socket.windows.DeferredSocketSend
import org.bread_experts_group.api.system.socket.windows.WindowsSocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.WSABUF
import org.bread_experts_group.ffi.windows.wsa.WSABUF_buf
import org.bread_experts_group.ffi.windows.wsa.WSABUF_len
import org.bread_experts_group.ffi.windows.wsa.nativeWSASend
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsIPv6SocketSendFeature(
	private val socket: Long,
	private val monitor: WindowsSocketMonitor,
	expresses: FeatureExpression<SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>>
) : SocketSendFeature<IPv6SendFeatureIdentifier, IPv6SendDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun scatterSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6SendFeatureIdentifier
	): DeferredSocketOperation<IPv6SendDataIdentifier> = object : DeferredSocketSend<IPv6SendDataIdentifier>(monitor) {
		override fun send(): List<IPv6SendDataIdentifier> = Arena.ofConfined().use { tempArena ->
			val allocated = tempArena.allocate(WSABUF, data.size.toLong())
			var currentSegment = allocated
			data.forEach {
				WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
				WSABUF_buf.set(currentSegment, 0L, it)
				currentSegment = currentSegment.asSlice(WSABUF.byteSize())
			}
			val status = nativeWSASend!!.invokeExact(
				capturedStateSegment,
				socket,
				allocated,
				data.size,
				threadLocalDWORD0,
				0, // TODO FLAGS
				MemorySegment.NULL,
				MemorySegment.NULL
			) as Int
			if (status != 0) throwLastWSAError()
			return emptyList()
		}
	}
}