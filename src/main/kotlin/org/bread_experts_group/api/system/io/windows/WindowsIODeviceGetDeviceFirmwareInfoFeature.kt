package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceGetDeviceFirmwareInfoFeature
import org.bread_experts_group.api.system.io.firmware_info.*
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.ioctl.*
import org.bread_experts_group.ffi.windows.nativeDeviceIoControl
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout

class WindowsIODeviceGetDeviceFirmwareInfoFeature(
	val handle: MemorySegment
) : IODeviceGetDeviceFirmwareInfoFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDeviceIoControl != null

	override fun get(
		vararg features: IODeviceGetFirmwareInfoFeatureIdentifier
	): List<IODeviceGetFirmwareInfoDataIdentifier> {
		val queryBuffer = autoArena.allocate(STORAGE_HW_FIRMWARE_INFO_QUERY)
		STORAGE_HW_FIRMWARE_INFO_QUERY_Version.set(queryBuffer, 0, STORAGE_HW_FIRMWARE_INFO_QUERY.byteSize().toInt())
		STORAGE_HW_FIRMWARE_INFO_QUERY_Size.set(queryBuffer, 0, queryBuffer.byteSize().toInt())
		var outputBuffer = autoArena.allocate(
			STORAGE_HW_FIRMWARE_INFO.byteSize() + STORAGE_HW_FIRMWARE_SLOT_INFO.byteSize()
		)
		val status = nativeDeviceIoControl!!.invokeExact(
			capturedStateSegment,
			handle,
			IOCTL_STORAGE_FIRMWARE_GET_INFO,
			queryBuffer,
			queryBuffer.byteSize().toInt(),
			outputBuffer,
			outputBuffer.byteSize().toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) throwLastError()
		val sizeCheck = (STORAGE_HW_FIRMWARE_INFO_Size.get(outputBuffer, 0) as Int).toLong()
		if (sizeCheck > outputBuffer.byteSize()) {
			outputBuffer = autoArena.allocate(sizeCheck)
			val status = nativeDeviceIoControl.invokeExact(
				capturedStateSegment,
				handle,
				IOCTL_STORAGE_FIRMWARE_GET_INFO,
				queryBuffer,
				queryBuffer.byteSize().toInt(),
				outputBuffer,
				outputBuffer.byteSize().toInt(),
				threadLocalDWORD0,
				MemorySegment.NULL
			) as Int
			if (status == 0) throwLastError()
		}
		val data = mutableListOf<IODeviceGetFirmwareInfoDataIdentifier>()
		val flags = (STORAGE_HW_FIRMWARE_INFO_Flags.get(outputBuffer, 0) as Byte).toInt()
		data.add(IODeviceFirmwareSupportsUpgrade(flags and 1 == 1))
		val shared = STORAGE_HW_FIRMWARE_INFO_FirmwareShared.get(outputBuffer, 0) as Int
		data.add(IODeviceFirmwareSharedDeviceAndAdapter(shared != 0))
		val slotCount = (STORAGE_HW_FIRMWARE_INFO_SlotCount.get(outputBuffer, 0) as Byte).toInt() and 0xFF
		if (slotCount > 0) {
			val slots = mutableMapOf<Int, List<IODeviceGetFirmwareInfoSlotDataIdentifier>>()
			repeat(slotCount) {
				val slotData = outputBuffer.asSlice(
					STORAGE_HW_FIRMWARE_INFO.byteSize() + (it * STORAGE_HW_FIRMWARE_SLOT_INFO.byteSize())
				)
				val flags = (STORAGE_HW_FIRMWARE_SLOT_INFO_Flags.get(slotData, 0) as Byte).toInt()
				slots[(STORAGE_HW_FIRMWARE_SLOT_INFO_SlotNumber.get(slotData, 0) as Byte).toInt() and 0xFF] = listOf(
					IODeviceFirmwareSlotReadOnly(flags and 1 == 1),
					IODeviceFirmwareSlotRevision(
						(STORAGE_HW_FIRMWARE_SLOT_INFO_Revision.invokeExact(slotData, 0L) as MemorySegment)
							.toArray(ValueLayout.JAVA_BYTE)
					)
				)
			}
			data.add(IODeviceFirmwareSlots(slots))
		}
		data.add(
			IODeviceFirmwareImagePayloadAlignment(
				STORAGE_HW_FIRMWARE_INFO_ImagePayloadAlignment.get(outputBuffer, 0) as Int
			)
		)
		data.add(
			IODeviceFirmwareImagePayloadMaxSize(
				STORAGE_HW_FIRMWARE_INFO_ImagePayloadMaxSize.get(outputBuffer, 0) as Int
			)
		)
		data.add(
			IODeviceFirmwarePendingActivationSlot(
				(STORAGE_HW_FIRMWARE_INFO_PendingActivateSlot.get(outputBuffer, 0) as Byte).toInt() and 0xFF
			)
		)
		data.add(
			IODeviceFirmwareActiveSlot(
				(STORAGE_HW_FIRMWARE_INFO_ActiveSlot.get(outputBuffer, 0) as Byte).toInt() and 0xFF
			)
		)
		return data
	}
}