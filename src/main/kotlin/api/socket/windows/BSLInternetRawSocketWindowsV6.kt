package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.api.socket.BSLInetSocketAddress
import org.bread_experts_group.api.socket.BSLInternetProtocolSocketType
import org.bread_experts_group.api.socket.SocketAlreadyBoundException
import org.bread_experts_group.api.socket.SocketNotBoundException
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.net.Inet6Address
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.NetworkChannel

class BSLInternetRawSocketWindowsV6 : BSLInternetRawSocketBaseWindows(
	BSLInternetProtocolSocketType.VERSION_6,
	WSAAddressFamily.AF_INET6,
	WSASocketType.SOCK_RAW,
	WSAProtocolDefinition.IPPROTO_IP
) {
	private val sockAddr = this.arena.allocate(wsaV6SockAddrInStruct)
	override fun bind(local: SocketAddress?): NetworkChannel? {
		this.localAddress?.let {
			if (it != local) throw SocketAlreadyBoundException("Already bound on $it")
			return this
		}
		if (local != null) {
			if (local !is BSLInetSocketAddress)
				throw UnsupportedOperationException("SocketAddress must be a BSLInetSocketAddress")
			val address = local.address.address
			if (address.size != 16)
				throw UnsupportedOperationException("SocketAddress data must be 16 bytes long")
			val sockAddrData = wSaV6AddrHandle.invokeExact(this.sockAddr) as MemorySegment
			address.forEachIndexed { index, byte ->
				sockAddrData.set(ValueLayout.JAVA_BYTE, index.toLong(), byte)
			}
		}
		wSaV6FamilyHandle.set(this.sockAddr, protocol.addressFamily.raw.toShort())
		val result = nativeBind.invokeExact(
			this.socketDescriptor,
			this.sockAddr,
			this.sockAddr.byteSize().toInt()
		) as Int
		if (result == -1) decodeSocketError()
		return this
	}

	private val sockAddrSize = this.arena.allocate(ValueLayout.JAVA_INT)
	override fun getLocalAddress(): SocketAddress? = try {
		sockAddrSize.set(ValueLayout.JAVA_INT, 0, sockAddr.byteSize().toInt())
		val result = nativeGetSockName.invokeExact(
			this.socketDescriptor,
			sockAddr,
			sockAddrSize
		) as Int
		if (result == -1) decodeSocketError()
		val data = wSaV6AddrHandle.invokeExact(this.sockAddr) as MemorySegment
		BSLInetSocketAddress(
			Inet6Address.getByAddress(
				data.asSlice(0, 16).toArray(ValueLayout.JAVA_BYTE)
			)
		)
	} catch (_: WSAInvalidArgumentException) {
		null
	}

	override fun writeDatagram(src: ByteBuffer): Pair<ByteArray, Int> {
		if (this.closed) throw ClosedChannelException()
		if (this.localAddress == null) throw SocketNotBoundException()
		TODO("v6 write")
	}
}