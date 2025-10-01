package org.bread_experts_group.api.serial.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.serial.SerialInterface
import org.bread_experts_group.api.serial.SerialParityScheme
import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.nio.ByteBuffer
import java.util.*

class WindowsSerialInterface : SerialInterface() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean {
		nativeCreateFile3
		nativeGetCommState
		nativeSetCommState
		nativeReadFile
		return true
	}

	private lateinit var serialHandle: MemorySegment
	override fun configure(
		device: UByte,
		baudRate: UInt,
		dataBits: UByte,
		stopBits: UByte,
		parityScheme: SerialParityScheme
	) = Arena.ofConfined().use { arena ->
		serialHandle = createFile3(
			arena,
			"\\\\.\\COM$device",
			EnumSet.of(
				WindowsGenericAccessRights.GENERIC_READ,
				WindowsGenericAccessRights.GENERIC_WRITE
			),
			EnumSet.noneOf(WindowsFileSharingTypes::class.java),
			WindowsCreationDisposition.OPEN_EXISTING
		)
		val dcb = SerialCommunicationDeviceControl(arena)
		if (nativeGetCommState!!.invokeExact(capturedStateSegment, serialHandle, dcb.ptr) as Int == 0)
			decodeLastError(arena)
		dcb.baudRate = WindowsBaudRate.entries.id(baudRate)
		dcb.dataBits = dataBits
		dcb.stopBits = MappedEnumeration(
			when (stopBits.toUInt()) {
				1u -> WindowsStopBits.ONESTOPBOT
				15u -> WindowsStopBits.ONE5STOPBITS
				2u -> WindowsStopBits.TWOSTOPBITS
				else -> throw UnsupportedOperationException("Stop bit count $stopBits is not supported")
			}
		)
		dcb.parityScheme = MappedEnumeration(
			when (parityScheme) {
				SerialParityScheme.NO_PARITY -> WindowsParityScheme.NOPARITY
			}
		)
		if (nativeSetCommState!!.invokeExact(capturedStateSegment, serialHandle, dcb.ptr) as Int == 0)
			decodeLastError(arena)
	}

	override fun read(b: MemorySegment, length: Long): Long = Arena.ofConfined().use { arena ->
		val read = arena.allocate(DWORD)
		if (
			nativeReadFile!!.invokeExact(
				capturedStateSegment,
				serialHandle,
				b,
				length.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
				read,
				MemorySegment.NULL
			) as Int == 0
		) decodeLastError(arena)
		return read.get(DWORD, 0).toLong()
	}

	override fun read(dst: ByteBuffer): Int {
		TODO("Not yet implemented")
	}

	override fun isOpen(): Boolean {
		TODO("Not yet implemented")
	}

	override fun close() {
		TODO("Not yet implemented")
	}
}