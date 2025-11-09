package org.bread_experts_group.api.io.serial.windows

import org.bread_experts_group.Flaggable.Companion.raw
import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.io.serial.IOSerialInterface
import org.bread_experts_group.api.io.serial.IOSerialParityScheme
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.util.*

class WindowsIOSerialInterface : IOSerialInterface() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeCreateFile3 != null &&
			nativeGetCommState != null &&
			nativeSetCommState != null &&
			nativeReadFile != null

	private lateinit var serialHandle: MemorySegment
	override fun configure(
		device: UByte,
		baudRate: UInt,
		dataBits: UByte,
		stopBits: UByte,
		parityScheme: IOSerialParityScheme
	) = Arena.ofConfined().use { arena ->
		serialHandle = nativeCreateFile3!!.invokeExact(
			capturedStateSegment,
			arena.allocateFrom("\\\\.\\COM$device", Charsets.UTF_16LE),
			EnumSet.of(
				WindowsGenericAccessRights.GENERIC_READ,
				WindowsGenericAccessRights.GENERIC_WRITE
			).raw().toInt(),
			EnumSet.noneOf(WindowsFileSharingTypes::class.java).raw().toInt(),
			WindowsCreationDisposition.OPEN_EXISTING.id.toInt(),
			MemorySegment.NULL
		) as MemorySegment
		if (serialHandle == INVALID_HANDLE_VALUE) decodeLastError()
		val dcb = SerialCommunicationDeviceControl(arena)
		if (nativeGetCommState!!.invokeExact(capturedStateSegment, serialHandle, dcb.ptr) as Int == 0)
			decodeLastError()
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
				IOSerialParityScheme.NO_PARITY -> WindowsParityScheme.NOPARITY
			}
		)
		if (nativeSetCommState!!.invokeExact(capturedStateSegment, serialHandle, dcb.ptr) as Int == 0)
			decodeLastError()
	}

//	override fun read(b: MemorySegment, length: Long): Long = Arena.ofConfined().use { arena ->
//		val read = arena.allocate(DWORD)
//		if (
//			nativeReadFile!!.invokeExact(
//				capturedStateSegment,
//				serialHandle,
//				b,
//				length.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
//				read,
//				MemorySegment.NULL
//			) as Int == 0
//		) decodeLastError()
//		return read.get(DWORD, 0).toLong()
//	}
}