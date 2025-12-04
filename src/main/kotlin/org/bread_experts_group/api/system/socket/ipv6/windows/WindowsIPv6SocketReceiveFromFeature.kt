package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.socket.feature.SocketReceiveFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.receive.IPv6ReceiveFeatureIdentifier
import org.bread_experts_group.api.system.socket.listen.ReceiveSizeData
import org.bread_experts_group.api.system.socket.listen.WindowsReceiveFeatures
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIPv6SocketReceiveFromFeature(
	private val socket: Long,
	expresses: FeatureExpression<SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>>
) : SocketReceiveFeature<IPv6ReceiveFeatureIdentifier, IPv6ReceiveDataIdentifier>(expresses) {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun gatherSegments(
		data: Collection<MemorySegment>,
		vararg features: IPv6ReceiveFeatureIdentifier
	): List<IPv6ReceiveDataIdentifier> = Arena.ofConfined().use { tempArena ->
		val supportedFeatures = mutableListOf<IPv6ReceiveDataIdentifier>()
		val allocated = tempArena.allocate(WSABUF, data.size.toLong())
		var currentSegment = allocated
		data.forEach {
			WSABUF_len.set(currentSegment, 0L, it.byteSize().toInt())
			WSABUF_buf.set(currentSegment, 0L, it)
			currentSegment = currentSegment.asSlice(WSABUF.byteSize())
		}
		var flags = 0
		if (features.contains(WindowsReceiveFeatures.PEEK)) {
			flags = flags or 0x2
			supportedFeatures.add(WindowsReceiveFeatures.PEEK)
		}
		threadLocalDWORD1.set(DWORD, 0, flags)
		val sender = tempArena.allocate(sockaddr_in6)
		threadLocalDWORD2.set(DWORD, 0, sender.byteSize().toInt())
		val status = nativeWSARecvFrom!!.invokeExact(
			capturedStateSegment,
			socket,
			allocated,
			data.size,
			threadLocalDWORD0,
			threadLocalDWORD1,
			sender,
			threadLocalDWORD2,
			MemorySegment.NULL,
			MemorySegment.NULL
		) as Int
		if (status != 0) throwLastWSAError()
		supportedFeatures.add(ReceiveSizeData(threadLocalDWORD0.get(DWORD, 0).toLong()))
		val addrSeg = sockaddr_in6_sin6_addr_Byte.invokeExact(sender, 0L) as MemorySegment
		val addrBytes = ByteArray(addrSeg.byteSize().toInt())
		MemorySegment.copy(
			addrSeg, ValueLayout.JAVA_BYTE, 0,
			addrBytes, 0, addrBytes.size
		)
		supportedFeatures.add(
			InternetProtocolV6AddressPortData(
				addrBytes,
				(sockaddr_in6_sin6_port.get(sender, 0L) as Short).toUShort()
			)
		)
		return supportedFeatures
	}
}