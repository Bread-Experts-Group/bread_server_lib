@file:Suppress("LongLine")

package org.bread_experts_group.api.system.socket.ipv6.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.AF_INET6
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.IPPROTO_TCP
import org.bread_experts_group.api.system.feature.windows.WindowsSystemNetworkingSocketsFeature.Companion.SOCK_STREAM
import org.bread_experts_group.api.system.socket.DeferredOperation
import org.bread_experts_group.api.system.socket.close.SocketCloseFeatureIdentifier
import org.bread_experts_group.api.system.socket.feature.SocketAcceptFeature
import org.bread_experts_group.api.system.socket.feature.SocketFeatureImplementation
import org.bread_experts_group.api.system.socket.ipv6.IPv6Socket
import org.bread_experts_group.api.system.socket.ipv6.IPv6SocketFeatures
import org.bread_experts_group.api.system.socket.ipv6.InternetProtocolV6AddressPortData
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptDataIdentifier
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptFeatureIdentifier
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptedLocalAddressPort
import org.bread_experts_group.api.system.socket.ipv6.accept.IPv6AcceptedRemoteAddressPort
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager.RECEIVE_OPERATION
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate_identification
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketEventManager.WSAOVERLAPPEDEncapsulate_operation
import org.bread_experts_group.api.system.socket.system.windows.WindowsSocketManager
import org.bread_experts_group.api.system.socket.system.windows.winClose
import org.bread_experts_group.ffi.*
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.ffi.windows.wsa.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.lang.invoke.MethodHandle
import java.util.concurrent.TimeUnit

class WindowsIPv6SocketAcceptFeature(
	private val socket: Long,
	private val manager: WindowsSocketManager,
	expresses: FeatureExpression<SocketAcceptFeature<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>>
) : SocketAcceptFeature<IPv6AcceptFeatureIdentifier, IPv6AcceptDataIdentifier>(expresses) {
	val nativeAcceptEx: MethodHandle = run {
		val status = nativeWSAIoctl!!.invokeExact(
			capturedStateSegment,
			socket,
			SIO_GET_EXTENSION_FUNCTION_POINTER,
			WSAID_ACCEPTEX,
			WSAID_ACCEPTEX.byteSize().toInt(),
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
				SOCKET.withName("sListenSocket"),
				SOCKET.withName("sAcceptSocket"),
				PVOID.withName("lpOutputBuffer"),
				DWORD.withName("dwReceiveDataLength"),
				DWORD.withName("dwLocalAddressLength"),
				DWORD.withName("dwRemoteAddressLength"),
				LPDWORD.withName("lpdwBytesReceived"),
				LPOVERLAPPED.withName("lpOverlapped")
			),
			listOf(
				gleCapture
			)
		)
	}

	val nativeGetAcceptExSockaddrs: MethodHandle = run {
		val status = nativeWSAIoctl!!.invokeExact(
			capturedStateSegment,
			socket,
			SIO_GET_EXTENSION_FUNCTION_POINTER,
			WSAID_GETACCEPTEXSOCKADDRS,
			WSAID_GETACCEPTEXSOCKADDRS.byteSize().toInt(),
			threadLocalPTR,
			threadLocalPTR.byteSize().toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL,
			MemorySegment.NULL
		) as Int
		if (status == SOCKET_ERROR) throwLastWSAError()
		threadLocalPTR.get(ValueLayout.ADDRESS, 0).getDowncallVoid(
			nativeLinker,
			PVOID.withName("lpOutputBuffer"),
			DWORD.withName("dwReceiveDataLength"),
			DWORD.withName("dwLocalAddressLength"),
			DWORD.withName("dwRemoteAddressLength"),
			ValueLayout.ADDRESS.withName("LocalSockaddr"), // sockaddr
			LPINT.withName("LocalSockaddrLength"),
			ValueLayout.ADDRESS.withName("RemoteSockaddr"), // sockaddr
			LPINT.withName("RemoteSockaddrLength")
		)
	}

	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun accept(
		vararg features: IPv6AcceptFeatureIdentifier
	): DeferredOperation<IPv6AcceptDataIdentifier> {
		val acceptData = mutableListOf<IPv6AcceptDataIdentifier>()
		val acceptArena = Arena.ofConfined()
		val (identification, semaphore, _) = manager.getSemaphore()
		val acceptingSocket: Long
		val ip6 = sockaddr_in6.byteSize().toInt() + 16
		val acceptDataSegment = acceptArena.allocate(ip6 * 2L)
		try {
			val overlapped = acceptArena.allocate(WSAOVERLAPPEDEncapsulate)
			WSAOVERLAPPEDEncapsulate_operation.set(overlapped, 0L, RECEIVE_OPERATION)
			WSAOVERLAPPEDEncapsulate_identification.set(overlapped, 0L, identification)
			acceptingSocket = nativeWSASocketWide!!.invokeExact(
				capturedStateSegment,
				AF_INET6, SOCK_STREAM, IPPROTO_TCP,
				MemorySegment.NULL, 0, 0x01
			) as Long
			if (acceptingSocket == INVALID_SOCKET) throwLastWSAError()
			val status = nativeAcceptEx.invokeExact(
				capturedStateSegment,
				socket,
				acceptingSocket,
				acceptDataSegment,
				0,
				ip6,
				ip6,
				MemorySegment.NULL,
				overlapped
			) as Int
			if (status == 0) {
				if (wsaLastError != WindowsLastError.ERROR_IO_PENDING.id.toInt()) throwLastWSAError()
			}
		} catch (e: Throwable) {
			acceptArena.close()
			manager.releaseSemaphore(identification, null)
			throw e
		}

		return object : DeferredOperation<IPv6AcceptDataIdentifier> {
			fun prepareData() {
				try {
					val manager = WindowsSocketEventManager.addSocket(acceptingSocket)
					acceptData.add(
						object : IPv6Socket() {
							override val features: MutableList<SocketFeatureImplementation<*>> = mutableListOf(
								WindowsIPv6SocketSendToFeature(
									acceptingSocket, manager, false,
									IPv6SocketFeatures.SEND
								),
								WindowsIPv6SocketReceiveFeature(
									acceptingSocket, manager,
									IPv6SocketFeatures.RECEIVE
								)
							)

							override fun close(
								vararg features: SocketCloseFeatureIdentifier
							) = winClose(acceptingSocket, *features)
						}
					)
					val localAddrPtr = acceptArena.allocate(ValueLayout.ADDRESS)
					val remoteAddrPtr = acceptArena.allocate(ValueLayout.ADDRESS)
					nativeGetAcceptExSockaddrs.invokeExact(
						acceptDataSegment,
						0,
						ip6,
						ip6,
						localAddrPtr,
						threadLocalDWORD0,
						remoteAddrPtr,
						threadLocalDWORD1
					)
					fun decodeAddress(ptr: MemorySegment, size: MemorySegment): InternetProtocolV6AddressPortData {
						val addr = ptr.get(ValueLayout.ADDRESS, 0)
							.reinterpret(size.get(DWORD, 0).toLong())
						val port = sockaddr_in6_sin6_port.get(addr, 0L) as Short
						val status = nativeWSAHtons!!.invokeExact(
							capturedStateSegment,
							socket,
							port,
							threadLocalDWORD2
						) as Int
						if (status != 0) throwLastWSAError()
						return InternetProtocolV6AddressPortData(
							(sockaddr_in6_sin6_addr_Byte.invokeExact(addr, 0L) as MemorySegment)
								.toArray(ValueLayout.JAVA_BYTE),
							threadLocalDWORD2.get(DWORD, 0).toUShort()
						)
					}
					acceptData.add(IPv6AcceptedLocalAddressPort(decodeAddress(localAddrPtr, threadLocalDWORD0)))
					acceptData.add(IPv6AcceptedRemoteAddressPort(decodeAddress(remoteAddrPtr, threadLocalDWORD1)))
				} finally {
					acceptArena.close()
				}
			}

			override fun block(): List<IPv6AcceptDataIdentifier> {
				semaphore.acquire()
				prepareData()
				return acceptData
			}

			override fun block(time: Long, unit: TimeUnit): List<IPv6AcceptDataIdentifier> {
				if (!semaphore.tryAcquire(time, unit)) return emptyList()
				prepareData()
				return acceptData
			}
		}
	}
}