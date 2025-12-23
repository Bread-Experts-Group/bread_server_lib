@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.stream.tcp.feature.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.feature.SocketConnectFeature
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.connect.IPv6ConnectionFeatureIdentifier
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.CONNECT_OPERATION
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate_identification
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate_operation
import org.bread_experts_group.api.system.socket.sys_feature.windows.WindowsSocketManager
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.getDowncall
import org.bread_experts_group.ffi.nativeLinker
import org.bread_experts_group.ffi.threadLocalPTR
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.util.concurrent.TimeUnit

class WindowsIPv6TCPConnectFeature(
	private val socket: Long,
	private val manager: WindowsSocketManager,
	expresses: FeatureExpression<SocketConnectFeature<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>>
) : SocketConnectFeature<IPv6ConnectionFeatureIdentifier, IPv6ConnectionDataIdentifier>(expresses) {
	val nativeLpfnConnectEx: MethodHandle = run {
		val status = nativeWSAIoctl!!.invokeExact(
			capturedStateSegment,
			socket,
			SIO_GET_EXTENSION_FUNCTION_POINTER,
			WSAID_CONNECTEX,
			WSAID_CONNECTEX.byteSize().toInt(),
			threadLocalPTR,
			threadLocalPTR.byteSize().toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL,
			MemorySegment.NULL
		) as Int
		if (status == SOCKET_ERROR) throwLastWSAError()
		threadLocalPTR.get(ValueLayout.ADDRESS, 0).getDowncall(
			nativeLinker,
			arrayOf(
				BOOL,
				SOCKET.withName("s"),
				ValueLayout.ADDRESS.withName("name"), /* sockaddr */
				int.withName("namelen"),
				PVOID.withName("lpSendBuffer"),
				DWORD.withName("dwSendDataLength"),
				LPDWORD.withName("lpdwBytesSent"),
				LPOVERLAPPED.withName("lpOverlapped")
			),
			listOf(
				gleCapture
			)
		)
	}

	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun connect(
		vararg features: IPv6ConnectionFeatureIdentifier
	): DeferredOperation<IPv6ConnectionDataIdentifier> {
		val connectData = mutableListOf<IPv6ConnectionDataIdentifier>()
		val connectArena = Arena.ofConfined()
		val (identification, semaphore, _) = manager.getSemaphore()
		try {
			val address = features.firstNotNullOfOrNull { it as? InternetProtocolV6AddressPortData }
				?: return DeferredOperation.Immediate(emptyList())
			connectData.add(address)
			val overlapped = connectArena.allocate(WSAOVERLAPPEDEncapsulate)
			WSAOVERLAPPEDEncapsulate_operation.set(overlapped, 0L, CONNECT_OPERATION)
			WSAOVERLAPPEDEncapsulate_identification.set(overlapped, 0L, identification)
			val sockAddr = connectArena.allocate(sockaddr_in6)
			sockaddr_in6_sin6_family.set(sockAddr, 0L, AF_INET6.toShort())
			var status = nativeWSAHtons!!.invokeExact(
				capturedStateSegment,
				socket,
				address.port.toShort(),
				threadLocalDWORD0
			) as Int
			if (status != 0) throwLastWSAError()
			sockaddr_in6_sin6_port.set(
				sockAddr, 0L,
				threadLocalDWORD0.get(ValueLayout.JAVA_SHORT, 0)
			)
			MemorySegment.copy(
				address.data, 0,
				(sockaddr_in6_sin6_addr_Byte.invokeExact(sockAddr, 0L) as MemorySegment),
				ValueLayout.JAVA_BYTE, 0, address.data.size
			)
			status = nativeLpfnConnectEx.invokeExact(
				capturedStateSegment,
				socket,
				sockAddr,
				sockAddr.byteSize().toInt(),
				MemorySegment.NULL,
				0,
				MemorySegment.NULL,
				overlapped
			) as Int
			if (status == 0) {
				if (wsaLastError != WindowsLastError.ERROR_IO_PENDING.id.toInt()) throwLastWSAError()
			}
		} catch (e: Throwable) {
			connectArena.close()
			manager.releaseSemaphore(identification, null)
			throw e
		}

		return object : DeferredOperation<IPv6ConnectionDataIdentifier> {
			override fun block(): List<IPv6ConnectionDataIdentifier> {
				semaphore.acquire()
				return connectData
			}

			override fun block(time: Long, unit: TimeUnit): List<IPv6ConnectionDataIdentifier> {
				if (!semaphore.tryAcquire(time, unit)) return emptyList()
				return connectData
			}
		}
	}
}