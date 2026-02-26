package org.bread_experts_group.ffi.windows.ioctl

import org.bread_experts_group.ffi.windows.BOOLEAN
import org.bread_experts_group.ffi.windows.UCHAR
import org.bread_experts_group.ffi.windows.ULONG
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

val STORAGE_HW_FIRMWARE_INFO_QUERY: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("Version"),
	ULONG.withName("Size"),
	ULONG.withName("Flags"),
	ULONG.withName("Reserved")
)
val STORAGE_HW_FIRMWARE_INFO_QUERY_Version: VarHandle = STORAGE_HW_FIRMWARE_INFO_QUERY.varHandle(
	groupElement("Version")
)
val STORAGE_HW_FIRMWARE_INFO_QUERY_Size: VarHandle = STORAGE_HW_FIRMWARE_INFO_QUERY.varHandle(
	groupElement("Size")
)

const val STORAGE_HW_FIRMWARE_REVISION_LENGTH = 16L
val STORAGE_HW_FIRMWARE_SLOT_INFO: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("Version"),
	ULONG.withName("Size"),
	UCHAR.withName("SlotNumber"),
	UCHAR.withName("Flags"),
	MemoryLayout.sequenceLayout(6, UCHAR).withName("Reserved"),
	MemoryLayout.sequenceLayout(STORAGE_HW_FIRMWARE_REVISION_LENGTH, UCHAR).withName("Revision")
)
val STORAGE_HW_FIRMWARE_SLOT_INFO_SlotNumber: VarHandle = STORAGE_HW_FIRMWARE_SLOT_INFO.varHandle(
	groupElement("SlotNumber")
)
val STORAGE_HW_FIRMWARE_SLOT_INFO_Flags: VarHandle = STORAGE_HW_FIRMWARE_SLOT_INFO.varHandle(
	groupElement("Flags")
)
val STORAGE_HW_FIRMWARE_SLOT_INFO_Revision: MethodHandle = STORAGE_HW_FIRMWARE_SLOT_INFO.sliceHandle(
	groupElement("Revision")
)

val STORAGE_HW_FIRMWARE_INFO: StructLayout = MemoryLayout.structLayout(
	ULONG.withName("Version"),
	ULONG.withName("Size"),
	UCHAR.withName("Flags"),
	UCHAR.withName("SlotCount"),
	UCHAR.withName("ActiveSlot"),
	UCHAR.withName("PendingActivateSlot"),
	BOOLEAN.withName("FirmwareShared"),
	MemoryLayout.sequenceLayout(3, UCHAR).withName("Reserved"),
	ULONG.withName("ImagePayloadAlignment"),
	ULONG.withName("ImagePayloadMaxSize")
)
val STORAGE_HW_FIRMWARE_INFO_Size: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("Size")
)
val STORAGE_HW_FIRMWARE_INFO_Flags: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("Flags")
)
val STORAGE_HW_FIRMWARE_INFO_SlotCount: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("SlotCount")
)
val STORAGE_HW_FIRMWARE_INFO_ActiveSlot: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("ActiveSlot")
)
val STORAGE_HW_FIRMWARE_INFO_PendingActivateSlot: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("PendingActivateSlot")
)
val STORAGE_HW_FIRMWARE_INFO_FirmwareShared: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("FirmwareShared")
)
val STORAGE_HW_FIRMWARE_INFO_ImagePayloadAlignment: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("ImagePayloadAlignment")
)
val STORAGE_HW_FIRMWARE_INFO_ImagePayloadMaxSize: VarHandle = STORAGE_HW_FIRMWARE_INFO.varHandle(
	groupElement("ImagePayloadMaxSize")
)