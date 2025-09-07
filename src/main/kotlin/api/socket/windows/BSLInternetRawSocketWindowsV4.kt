package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.api.socket.*
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.net.Inet4Address
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException

class BSLInternetRawSocketWindowsV4 : BSLInternetRawSocketBaseWindows(
	BSLInternetProtocolSocketType.VERSION_4,
	WSAAddressFamily.AF_INET,
	WSASocketType.SOCK_RAW,
	WSAProtocolDefinition.IPPROTO_IP
) {
	private val sockAddr = this.arena.allocate(wsaV4SockAddrStruct)
	override fun bind(local: SocketAddress?): BSLInternetRawSocket {
		this.localAddress?.let {
			if (it != local) throw SocketAlreadyBoundException("Already bound on $it")
			return this
		}
		if (local != null) {
			if (local !is BSLInetSocketAddress)
				throw UnsupportedOperationException("SocketAddress must be a BSLInetSocketAddress")
			val address = local.address.address
			if (address.size != 4)
				throw UnsupportedOperationException("SocketAddress data must be 4 bytes long")
			val sockAddrData = wSaV4AddrHandle.invokeExact(this.sockAddr) as MemorySegment
			address.forEachIndexed { index, byte ->
				sockAddrData.set(ValueLayout.JAVA_BYTE, index.toLong(), byte)
			}
		}
		wSaV4FamilyHandle.set(this.sockAddr, protocol.addressFamily.raw.toShort())
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
		val data = wSaV4AddrHandle.invokeExact(this.sockAddr) as MemorySegment
		BSLInetSocketAddress(
			Inet4Address.getByAddress(
				data.asSlice(0, 4).toArray(ValueLayout.JAVA_BYTE)
			)
		)
	} catch (_: WSAInvalidArgumentException) {
		null
	}

	override fun writeDatagram(src: ByteBuffer): Pair<ByteArray, Int> {
		if (this.closed) throw ClosedChannelException()
		if (this.localAddress == null) throw SocketNotBoundException()
		TODO("v4 write")
	}
}