package org.bread_experts_group.socket.windows

import org.bread_experts_group.socket.BSLBluetoothSocket
import org.bread_experts_group.socket.NoSocketAvailableException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.net.SocketAddress
import java.net.SocketOption
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.NetworkChannel
import java.nio.channels.NotYetBoundException
import java.util.*

class BSLBluetoothSocketWindows : BSLBluetoothSocket(arrayOf("Windows 11")) {
	val arena: Arena = Arena.ofConfined()
	var closed = false
	val protocol = wsaProtocols.list.firstOrNull {
		it.socketType.enum == WSASocketType.SOCK_STREAM && it.addressFamily.enum == WSAAddressFamily.AF_BTH
	} ?: throw NoSocketAvailableException(
		"The system does not appear to have a catalog entry for ${WSASocketType.SOCK_STREAM} " +
				"under ${WSAAddressFamily.AF_BTH}."
	)
	val socketDescriptor: Int = wsaSocket(
		WSAAddressFamily.AF_BTH, WSASocketType.SOCK_STREAM, WSAProtocolDefinition.BTHPROTO_RFCOMM.id,
		protocol.pointer, 0, EnumSet.noneOf(WSASocketFlags::class.java)
	)

	private val flags = this.arena.allocate(ValueLayout.JAVA_INT)
	private val rxCount = this.arena.allocate(ValueLayout.JAVA_INT)

	override fun readDatagram(dst: ByteBuffer): Pair<ByteArray, Int> {
		// TODO: Consolidate sock read
		if (closed) throw ClosedChannelException()
		if (this.localAddress == null) throw NotYetBoundException()
		val networkArena = Arena.ofConfined()
		val sourceAddr = networkArena.allocate(protocol.addressMax.toLong())
		val sourceAddrLength = networkArena.allocate(ValueLayout.JAVA_INT, sourceAddr.byteSize().toInt())
		val rxBufferArray = networkArena.allocateArray(wsaBufferStruct, 1)
		val rxBuffer = networkArena.allocate(dst.remaining().toLong())
		wLenHandle.set(rxBufferArray, rxBuffer.byteSize().toInt())
		wBufHandle.set(rxBufferArray, rxBuffer)
		val result = nativeWSARecvFrom.invokeExact(
			this.socketDescriptor,
			rxBufferArray,
			1,
			rxCount,
			flags,
			sourceAddr,
			sourceAddrLength,
			MemorySegment.NULL,
			MemorySegment.NULL
		) as Int
		if (result == -1) decodeSocketError()
		val read = rxCount.get(ValueLayout.JAVA_INT, 0)
		dst.put(
			rxBuffer
				.reinterpret(read.toLong())
				.toArray(ValueLayout.JAVA_BYTE)
		)
		val address = sourceAddr.reinterpret(
			sourceAddrLength.get(ValueLayout.JAVA_INT, 0).toLong()
		).toArray(ValueLayout.JAVA_BYTE)
		networkArena.close()
		return address to read
	}

	override fun writeDatagram(src: ByteBuffer): Pair<ByteArray, Int> {
		TODO("Not yet implemented")
	}

	override fun isOpen(): Boolean = !closed
	override fun close() {
		if (closed) return
		closed = true
		arena.close()
		val result = nativeCloseSocket.invokeExact(this.socketDescriptor) as Int
		if (result == -1) decodeSocketError()
	}

	private val sockAddr = this.arena.allocate(wsaV4SockAddrStruct)
	override fun bind(local: SocketAddress?): BSLBluetoothSocketWindows {
//		this.localAddress?.let {
//			if (it != local) throw SocketAlreadyBoundException("Already bound on $it")
//			return this
//		}
//		if (local != null) {
//			if (local !is BSLBluetoothSocketAddress)
//				throw UnsupportedOperationException("SocketAddress must be a BSLBluetoothSocketAddress")
//			val sockAddrData = wSaV4AddrHandle.invokeExact(this.sockAddr) as MemorySegment
//			local.address.forEachIndexed { index, byte ->
//				sockAddrData.set(ValueLayout.JAVA_BYTE, index.toLong(), byte)
//			}
//		}
//		wSaV4FamilyHandle.set(this.sockAddr, protocol.addressFamily.raw.toShort())
//		val result = nativeBind.invokeExact(
//			this.socketDescriptor,
//			this.sockAddr,
//			this.sockAddr.byteSize().toInt()
//		) as Int
//		if (result == -1) decodeSocketError()
//		return this
		TODO("RFComm bind")
	}

	private val sockAddrSize = this.arena.allocate(ValueLayout.JAVA_INT)
	override fun getLocalAddress(): SocketAddress? = try {
//		sockAddrSize.set(ValueLayout.JAVA_INT, 0, sockAddr.byteSize().toInt())
//		val result = nativeGetSockName.invokeExact(
//			this.socketDescriptor,
//			sockAddr,
//			sockAddrSize
//		) as Int
//		if (result == -1) decodeSocketError()
//		val data = wSaV4AddrHandle.invokeExact(this.sockAddr) as MemorySegment
//		BSLBluetoothSocketAddress(
//			data.asSlice(0, 6).toArray(ValueLayout.JAVA_BYTE)
//		)

		TODO("RFComm locaddr")
	} catch (_: WSAInvalidArgumentException) {
		null
	}

	override fun <T : Any?> setOption(
		name: SocketOption<T?>?,
		value: T?
	): NetworkChannel? {
		TODO("Not yet implemented")
	}

	override fun <T : Any?> getOption(name: SocketOption<T?>?): T? {
		TODO("Not yet implemented")
	}

	override fun supportedOptions(): Set<SocketOption<*>?>? {
		TODO("Not yet implemented")
	}
}