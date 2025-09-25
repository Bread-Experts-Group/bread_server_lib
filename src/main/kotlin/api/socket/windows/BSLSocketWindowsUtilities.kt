package org.bread_experts_group.api.socket.windows

import org.bread_experts_group.api.socket.AddressNotAvailableException
import org.bread_experts_group.api.socket.NoSocketAvailableException
import org.bread_experts_group.api.socket.SocketAccessDeniedException
import org.bread_experts_group.channel.array
import org.bread_experts_group.coder.Flaggable.Companion.from
import org.bread_experts_group.coder.Flaggable.Companion.raw
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.ffi.windows.WindowsGUID
import org.bread_experts_group.ffi.windows.makeWord
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import java.nio.ByteOrder
import java.util.*

fun decodeExtendedError(error: Int) = when (error) {
	0 -> {}
	10013 -> throw SocketAccessDeniedException()
	10022 -> throw WSAInvalidArgumentException()
	10044 -> throw NoSocketAvailableException(
		"The system could not create a socket conforming to the provided parameters."
	)

	10049 -> throw AddressNotAvailableException()
	10055 -> throw WSANoBufferSpaceAvailableException()
	else -> throw UnsupportedOperationException("$error")
}

fun decodeSocketError() {
	decodeExtendedError(nativeWSAGetLastError() as Int)
}

private var wsaDataReal: WSAData? = null
val wsaData: WSAData
	get() {
		if (wsaDataReal == null) {
			val arena = Arena.ofConfined()
			val wsaDataSegment = arena.allocate(wsaDataStruct)
			val errorCode = nativeWSAStartup.invokeExact(
				makeWord(2u, 2u).toShort(),
				wsaDataSegment
			) as Int
			decodeExtendedError(errorCode)
			wsaDataReal = WSAData(
				WSAVersion((wVersionHandle.get(wsaDataSegment) as Short).toUShort()),
				WSAVersion((wHighVersionHandle.get(wsaDataSegment) as Short).toUShort()),
			)
			arena.close()
		}
		return wsaDataReal!!
	}

@OptIn(ExperimentalUnsignedTypes::class)
val wsaProtocols: WSAProtocolManagedList by lazy {
	wsaData
	val arena = Arena.ofConfined()
	val bufferSize = arena.allocate(ValueLayout.JAVA_INT)
	val errNo = arena.allocate(ValueLayout.JAVA_INT)
	try {
		val returned = nativeWSCEnumProtocols.invokeExact(
			MemorySegment.NULL,
			MemorySegment.NULL,
			bufferSize,
			errNo
		) as Int
		if (returned < 0) decodeExtendedError(errNo.get(ValueLayout.JAVA_INT, 0))
	} catch (_: WSANoBufferSpaceAvailableException) {
	}
	val data = arena.allocate(bufferSize.get(ValueLayout.JAVA_INT, 0).toLong())
	val returned = nativeWSCEnumProtocols.invokeExact(
		MemorySegment.NULL,
		data,
		bufferSize,
		errNo
	) as Int
	if (returned == -1) decodeExtendedError(errNo.get(ValueLayout.JAVA_INT, 0))
	val buffer = data.asByteBuffer()
	buffer.order(ByteOrder.nativeOrder())
	val protocols = List(returned) {
		val initialPosition = buffer.position()
		val sf1 = WSAProtocolServiceFlags1.entries.from(buffer.int)
		val sf2 = buffer.int
		val sf3 = buffer.int
		val sf4 = buffer.int
		val pf = WSAProviderFlags.entries.from(buffer.int)
		val guid = WindowsGUID(
			buffer.int.toUInt(),
			buffer.short.toUShort(),
			buffer.short.toUShort(),
			buffer.array(2).toUByteArray(),
			buffer.array(6).toUByteArray()
		)
		val catalogID = buffer.int
		val protocolLen = buffer.int
		val protocolChain = List(7) { buffer.int }
		WSAProtocol(
			sf1, sf2, sf3, sf4, pf,
			guid, catalogID, protocolChain.slice(0..protocolLen),
			buffer.int,
			WSAAddressFamily.entries.id(buffer.int),
			buffer.int,
			buffer.int,
			WSASocketType.entries.id(buffer.int),
			WSAProtocolDefinition.entries.id(buffer.int),
			buffer.int,
			when (val order = buffer.int) {
				0 -> ByteOrder.BIG_ENDIAN
				1 -> ByteOrder.LITTLE_ENDIAN
				else -> throw UnsupportedOperationException("$order byte order")
			},
			WSASecurityScheme.entries.id(buffer.int),
			buffer.int.toUInt(),
			buffer.int.toUInt(),
			buildString {
				val utf16 = buffer.asShortBuffer()
				while (true) {
					val next = utf16.get()
					if (next == 0.toShort()) break
					append(Char(next.toUShort()))
				}
				buffer.position(buffer.position() + 512)
			},
			data.asSlice(initialPosition.toLong())
		)
	}
	WSAProtocolManagedList(protocols, arena)
}

fun wsaSocket(
	addressFamily: WSAAddressFamily,
	socketType: WSASocketType,
	protocol: Int,
	info: MemorySegment,
	group: Int,
	flags: EnumSet<WSASocketFlags>
): Int {
	val descriptor = nativeWSASocketW.invokeExact(
		addressFamily.id, socketType.id, protocol,
		info, group, flags.raw().toInt()
	) as Int
	if (descriptor == -1) decodeSocketError()
	return descriptor
}