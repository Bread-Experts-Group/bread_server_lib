package org.bread_experts_group.api.system.socket.ipv4.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv4.receive.IPv4ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.listen.WindowsReceiveFeatures
import org.bread_experts_group.api.system.socket.receive.ReceiveSizeData
import org.bread_experts_group.api.system.socket.system.DeferredSocketReceive
import org.bread_experts_group.api.system.socket.system.SocketMonitor
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.threadLocalDWORD1
import org.bread_experts_group.ffi.windows.throwLastWSAError
import org.bread_experts_group.ffi.windows.wsa.WSABUF
import org.bread_experts_group.ffi.windows.wsa.WSABUF_buf
import org.bread_experts_group.ffi.windows.wsa.WSABUF_len
import org.bread_experts_group.ffi.windows.wsa.nativeWSARecv
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsIPv4SocketReceiveFeature(
	private val socket: Long,
	private val monitor: SocketMonitor,
	expresses: FeatureExpression<SocketReceiveFeature<IPv4ReceiveFeatureIdentifier, IPv4ReceiveDataIdentifier>>
) : SocketReceiveFeature<IPv4ReceiveFeatureIdentifier, IPv4ReceiveDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv4ReceiveFeatureIdentifier
	): DeferredSocketOperation<IPv4ReceiveDataIdentifier> =
		object : DeferredSocketReceive<IPv4ReceiveDataIdentifier>(monitor) {
			override fun receive(): List<IPv4ReceiveDataIdentifier> = Arena.ofConfined().use { tempArena ->
				val supportedFeatures = mutableListOf<IPv4ReceiveDataIdentifier>()
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
				if (status != 0) throwLastWSAError()
				supportedFeatures.add(ReceiveSizeData(threadLocalDWORD0.get(DWORD, 0).toLong()))
				return supportedFeatures
			}
		}
}