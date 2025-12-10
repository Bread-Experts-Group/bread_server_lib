package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.StandardSocketStatus
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.listen.WindowsReceiveFeatures
import org.bread_experts_group.api.system.socket.system.DeferredReceive
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.WSABUF
import org.bread_experts_group.ffi.windows.wsa.WSABUF_buf
import org.bread_experts_group.ffi.windows.wsa.WSABUF_len
import org.bread_experts_group.ffi.windows.wsa.nativeWSARecv
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsIPv6SocketReceiveFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>>
) : SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6ReceiveFeatureIdentifier
	): DeferredOperation<IPv6ReceiveDataIdentifier> =
		object : DeferredReceive<IPv6ReceiveDataIdentifier>(monitor) {
			override fun receive(): List<IPv6ReceiveDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val supportedFeatures = mutableListOf<IPv6ReceiveDataIdentifier>()
				val allocated = tempArena.allocate(WSABUF, data.size.toLong())
				var currentSegment = allocated
				data.forEach {
					WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
					WSABUF_buf.set(currentSegment, 0L, it)
					currentSegment = currentSegment.asSlice(WSABUF.byteSize())
				}
				var flags = 0
				if (features.contains(WindowsReceiveFeatures.OUT_OF_BAND)) {
					flags = flags or 0x1
					supportedFeatures.add(WindowsReceiveFeatures.OUT_OF_BAND)
				}
				if (features.contains(WindowsReceiveFeatures.PEEK)) {
					flags = flags or 0x2
					supportedFeatures.add(WindowsReceiveFeatures.PEEK)
				}
				if (features.contains(WindowsReceiveFeatures.WAIT_UNTIL_BUFFER_FULL)) {
					flags = flags or 0x8
					supportedFeatures.add(WindowsReceiveFeatures.WAIT_UNTIL_BUFFER_FULL)
				}
				if (features.contains(WindowsReceiveFeatures.HINT_NO_DELAY)) {
					flags = flags or 0x20
					supportedFeatures.add(WindowsReceiveFeatures.HINT_NO_DELAY)
				}
				if (features.contains(WindowsReceiveFeatures.PARTIAL)) {
					flags = flags or 0x8000
					supportedFeatures.add(WindowsReceiveFeatures.PARTIAL)
				}
				threadLocalDWORD0.set(DWORD, 0, 0)
				threadLocalDWORD1.set(DWORD, 0, flags)
				val status = nativeWSARecv!!.invokeExact(
					capturedStateSegment,
					socket,
					allocated,
					data.size,
					threadLocalDWORD0,
					threadLocalDWORD1,
					MemorySegment.NULL,
					MemorySegment.NULL
				) as Int
				if (status != 0) {
					when (wsaLastError) {
						10054 -> supportedFeatures.add(StandardSocketStatus.CONNECTION_CLOSED)
						10035 -> {}
						else -> throwLastWSAError()
					}
				}
				supportedFeatures.add(ReceiveSizeData(threadLocalDWORD0.get(DWORD, 0)))
				return supportedFeatures
			}
		}
}