package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.api.socket.BSLInternetProtocolSocketType
import org.bread_experts_group.api.socket.BSLInternetRawSocket
import org.bread_experts_group.api.socket.NoSocketAvailableException
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.net.SocketOption
import java.nio.ByteBuffer
import java.nio.channels.ClosedChannelException
import java.nio.channels.NetworkChannel
import java.nio.channels.NotYetBoundException
import java.util.*

sealed class BSLInternetRawSocketBaseWindows(
	internetType: BSLInternetProtocolSocketType,
	addressFamily: WSAAddressFamily,
	socketType: WSASocketType,
	protocolDefinition: WSAProtocolDefinition
) : BSLInternetRawSocket(internetType, arrayOf("Windows 11")) {
	protected val arena: Arena = Arena.ofConfined()
	protected var closed = false
	val protocol = wsaProtocols.list.firstOrNull {
		it.socketType.enum == socketType && it.addressFamily.enum == addressFamily
	} ?: throw NoSocketAvailableException(
		"The system does not appear to have a catalog entry for $socketType under $addressFamily."
	)
	protected val socketDescriptor: Int = wsaSocket(
		addressFamily, socketType, protocolDefinition.id,
		protocol.pointer, 0, EnumSet.noneOf(WSASocketFlags::class.java)
	)

	override fun promiscuous(toggle: Boolean) {
		val ioctlArena = Arena.ofConfined()
		val inputBuffer = ioctlArena.allocate(ValueLayout.JAVA_INT)
		inputBuffer.set(ValueLayout.JAVA_INT, 0, if (toggle) 1 else 0)
		val result = nativeWSAIoctl.invokeExact(
			this.socketDescriptor,
			(0x98000001).toInt(), // dwIoControlCode for SIO_RCVALL
			inputBuffer,
			4,
			MemorySegment.NULL,
			0,
			ioctlArena.allocate(ValueLayout.JAVA_INT),
			MemorySegment.NULL,
			MemorySegment.NULL
		) as Int
		if (result == -1) decodeSocketError()
		ioctlArena.close()
	}

	private val flags = this.arena.allocate(ValueLayout.JAVA_INT)
	private val rxCount = this.arena.allocate(ValueLayout.JAVA_INT)

	override fun readDatagram(dst: ByteBuffer): Pair<ByteArray, Int> {
		if (closed) throw ClosedChannelException()
		if (this.localAddress == null) throw NotYetBoundException()
		val networkArena = Arena.ofConfined()
		val sourceAddr = networkArena.allocate(protocol.addressMax.toLong())
		val sourceAddrLength = networkArena.allocateFrom(ValueLayout.JAVA_INT, sourceAddr.byteSize().toInt())
		val rxBufferArray = networkArena.allocate(wsaBufferStruct, 1)
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

	override fun isOpen(): Boolean = !closed
	override fun close() {
		if (closed) return
		closed = true
		arena.close()
		val result = nativeCloseSocket.invokeExact(this.socketDescriptor) as Int
		if (result == -1) decodeSocketError()
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