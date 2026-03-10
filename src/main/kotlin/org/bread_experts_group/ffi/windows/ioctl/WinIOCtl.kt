package org.bread_experts_group.ffi.windows.ioctl

import org.bread_experts_group.ffi.GUID
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_DISK = GUID(
	0x53F56307u,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_CDROM = GUID(
	0x53F56308u,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_PARTITION = GUID(
	0x53F5630Au,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_TAPE = GUID(
	0x53F5630Bu,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_WRITEONCEDISK = GUID(
	0x53F5630Cu,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_VOLUME = GUID(
	0x53F5630Du,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_MEDIUMCHANGER = GUID(
	0x53F56310u,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_FLOPPY = GUID(
	0x53F56311u,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@OptIn(ExperimentalUnsignedTypes::class)
val GUID_DEVINTERFACE_CDCHANGER = GUID(
	0x53F56312u,
	0xB6BFu,
	0x11D0u,
	ubyteArrayOf(0x94u, 0xF2u),
	ubyteArrayOf(0x00u, 0xA0u, 0xC9u, 0x1Eu, 0xFBu, 0x8Bu)
)

@Suppress("FunctionName")
fun CTL_CODE(deviceType: Int, function: Int, method: Int, access: Int): Int =
	(deviceType shl 16) or (access shl 14) or (function shl 2) or method

const val FILE_DEVICE_DISK = 0x00000007
const val FILE_DEVICE_FILE_SYSTEM = 0x00000009
const val FILE_DEVICE_MASS_STORAGE = 0x0000002D

const val METHOD_BUFFERED = 0
const val METHOD_NEITHER = 3

const val FILE_ANY_ACCESS = 0
const val FILE_READ_ACCESS = 0x0001

const val IOCTL_DISK_BASE = FILE_DEVICE_DISK

val FSCTL_ALLOW_EXTENDED_DASD_IO = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 32, METHOD_NEITHER, FILE_ANY_ACCESS)

val IOCTL_DISK_GET_DRIVE_LAYOUT_EX = CTL_CODE(IOCTL_DISK_BASE, 0x0014, METHOD_BUFFERED, FILE_ANY_ACCESS)
val IOCTL_DISK_GET_LENGTH_INFO = CTL_CODE(IOCTL_DISK_BASE, 0x0017, METHOD_BUFFERED, FILE_READ_ACCESS)
val IOCTL_DISK_GET_DRIVE_GEOMETRY_EX = CTL_CODE(IOCTL_DISK_BASE, 0x0028, METHOD_BUFFERED, FILE_ANY_ACCESS)

const val IOCTL_STORAGE_BASE = FILE_DEVICE_MASS_STORAGE

val IOCTL_STORAGE_FIRMWARE_GET_INFO = CTL_CODE(IOCTL_STORAGE_BASE, 0x0700, METHOD_BUFFERED, FILE_ANY_ACCESS)

val DISK_GEOMETRY: StructLayout = MemoryLayout.structLayout(
	LARGE_INTEGER.withName("Cylinders"),
	MEDIA_TYPE.withName("MediaType"),
	DWORD.withName("TracksPerCylinder"),
	DWORD.withName("SectorsPerTrack"),
	DWORD.withName("BytesPerSector")
)
val DISK_GEOMETRY_Cylinders: VarHandle = DISK_GEOMETRY.varHandle(groupElement("Cylinders"))
val DISK_GEOMETRY_TracksPerCylinder: VarHandle = DISK_GEOMETRY.varHandle(groupElement("TracksPerCylinder"))
val DISK_GEOMETRY_SectorsPerTrack: VarHandle = DISK_GEOMETRY.varHandle(groupElement("SectorsPerTrack"))
val DISK_GEOMETRY_BytesPerSector: VarHandle = DISK_GEOMETRY.varHandle(groupElement("BytesPerSector"))

val DISK_GEOMETRY_EX: StructLayout = MemoryLayout.structLayout(
	DISK_GEOMETRY.withName("Geometry"),
	LARGE_INTEGER.withName("DiskSize"),
	MemoryLayout.sequenceLayout(1, BYTE).withName("Data")
)
val DISK_GEOMETRY_EX_Geometry: MethodHandle = DISK_GEOMETRY_EX.sliceHandle(groupElement("Geometry"))
val DISK_GEOMETRY_EX_DiskSize: VarHandle = DISK_GEOMETRY_EX.varHandle(groupElement("DiskSize"))

val DRIVE_LAYOUT_INFORMATION_MBR: StructLayout = MemoryLayout.structLayout(
	BYTE.withName("PartitionType"),
	BOOLEAN.withName("BootIndicator"),
	BOOLEAN.withName("RecognizedPartition"),
	MemoryLayout.paddingLayout(1),
	DWORD.withName("HiddenSectors"),
	GUID.withName("PartitionId")
)
val DRIVE_LAYOUT_INFORMATION_MBR_PartitionType: VarHandle = DRIVE_LAYOUT_INFORMATION_MBR.varHandle(
	groupElement("PartitionType")
)
val DRIVE_LAYOUT_INFORMATION_MBR_BootIndicator: VarHandle = DRIVE_LAYOUT_INFORMATION_MBR.varHandle(
	groupElement("BootIndicator")
)
val DRIVE_LAYOUT_INFORMATION_MBR_RecognizedPartition: VarHandle = DRIVE_LAYOUT_INFORMATION_MBR.varHandle(
	groupElement("RecognizedPartition")
)
val DRIVE_LAYOUT_INFORMATION_MBR_HiddenSectors: VarHandle = DRIVE_LAYOUT_INFORMATION_MBR.varHandle(
	groupElement("HiddenSectors")
)
val DRIVE_LAYOUT_INFORMATION_MBR_PartitionId: MethodHandle = DRIVE_LAYOUT_INFORMATION_MBR.sliceHandle(
	groupElement("PartitionId")
)

val DRIVE_LAYOUT_INFORMATION_GPT: StructLayout = MemoryLayout.structLayout(
	GUID.withName("DiskId"),
	LARGE_INTEGER.withName("StartingUsableOffset"),
	LARGE_INTEGER.withName("UsableLength"),
	DWORD.withName("MaxPartitionCount")
)
val DRIVE_LAYOUT_INFORMATION_GPT_DiskId: MethodHandle = DRIVE_LAYOUT_INFORMATION_GPT.sliceHandle(
	groupElement("DiskId")
)
val DRIVE_LAYOUT_INFORMATION_GPT_StartingUsableOffset: VarHandle = DRIVE_LAYOUT_INFORMATION_GPT.varHandle(
	groupElement("StartingUsableOffset")
)
val DRIVE_LAYOUT_INFORMATION_GPT_UsableLength: VarHandle = DRIVE_LAYOUT_INFORMATION_GPT.varHandle(
	groupElement("UsableLength")
)
val DRIVE_LAYOUT_INFORMATION_GPT_MaxPartitionCount: VarHandle = DRIVE_LAYOUT_INFORMATION_GPT.varHandle(
	groupElement("MaxPartitionCount")
)

val DRIVE_LAYOUT_INFORMATION_EX: StructLayout = MemoryLayout.structLayout(
	DWORD.withName("PartitionStyle"),
	DWORD.withName("PartitionCount"),
	MemoryLayout.unionLayout(
		DRIVE_LAYOUT_INFORMATION_MBR.withName("Mbr"),
		DRIVE_LAYOUT_INFORMATION_GPT.withName("Gpt")
	).withName("DUMMYUNIONNAME"),
	MemoryLayout.paddingLayout(4)
)
val DRIVE_LAYOUT_INFORMATION_EX_PartitionStyle: VarHandle = DRIVE_LAYOUT_INFORMATION_EX.varHandle(
	groupElement("PartitionStyle")
)
val DRIVE_LAYOUT_INFORMATION_EX_PartitionCount: VarHandle = DRIVE_LAYOUT_INFORMATION_EX.varHandle(
	groupElement("PartitionCount")
)
val DRIVE_LAYOUT_INFORMATION_EX_DUMMYUNIONNAME: MethodHandle = DRIVE_LAYOUT_INFORMATION_EX.sliceHandle(
	groupElement("DUMMYUNIONNAME")
)

val PARTITION_INFORMATION_MBR: StructLayout = MemoryLayout.structLayout(
	BYTE.withName("PartitionType"),
	BOOLEAN.withName("BootIndicator"),
	BOOLEAN.withName("RecognizedPartition"),
	MemoryLayout.paddingLayout(1),
	DWORD.withName("HiddenSectors"),
	GUID.withName("PartitionId")
)
val PARTITION_INFORMATION_MBR_PartitionType: VarHandle = PARTITION_INFORMATION_MBR.varHandle(
	groupElement("PartitionType")
)
val PARTITION_INFORMATION_MBR_BootIndicator: VarHandle = PARTITION_INFORMATION_MBR.varHandle(
	groupElement("BootIndicator")
)
val PARTITION_INFORMATION_MBR_RecognizedPartition: VarHandle = PARTITION_INFORMATION_MBR.varHandle(
	groupElement("RecognizedPartition")
)
val PARTITION_INFORMATION_MBR_HiddenSectors: VarHandle = PARTITION_INFORMATION_MBR.varHandle(
	groupElement("HiddenSectors")
)
val PARTITION_INFORMATION_MBR_PartitionId: MethodHandle = PARTITION_INFORMATION_MBR.sliceHandle(
	groupElement("PartitionId")
)

val PARTITION_INFORMATION_GPT: StructLayout = MemoryLayout.structLayout(
	GUID.withName("PartitionType"),
	GUID.withName("PartitionId"),
	DWORD64.withName("Attributes"),
	MemoryLayout.sequenceLayout(36, WCHAR).withName("Name")
)
val PARTITION_INFORMATION_GPT_PartitionType: MethodHandle = PARTITION_INFORMATION_GPT.sliceHandle(
	groupElement("PartitionType")
)
val PARTITION_INFORMATION_GPT_PartitionId: MethodHandle = PARTITION_INFORMATION_GPT.sliceHandle(
	groupElement("PartitionId")
)
val PARTITION_INFORMATION_GPT_Attributes: VarHandle = PARTITION_INFORMATION_GPT.varHandle(
	groupElement("Attributes")
)
val PARTITION_INFORMATION_GPT_Name: MethodHandle = PARTITION_INFORMATION_GPT.sliceHandle(
	groupElement("Name")
)

val PARTITION_INFORMATION_EX: StructLayout = MemoryLayout.structLayout(
	PARTITION_STYLE.withName("PartitionStyle"),
	WORD.withName("PartitionOrdinal"),
	MemoryLayout.paddingLayout(2),
	LARGE_INTEGER.withName("StartingOffset"),
	LARGE_INTEGER.withName("PartitionLength"),
	DWORD.withName("PartitionNumber"),
	BOOLEAN.withName("RewritePartition"),
	BOOLEAN.withName("IsServicePartition"),
	MemoryLayout.paddingLayout(2),
	MemoryLayout.unionLayout(
		PARTITION_INFORMATION_MBR.withName("Mbr"),
		PARTITION_INFORMATION_GPT.withName("Gpt")
	).withName("DUMMYUNIONNAME")
)
val PARTITION_INFORMATION_EX_PartitionOrdinal: VarHandle = PARTITION_INFORMATION_EX.varHandle(
	groupElement("PartitionOrdinal")
)
val PARTITION_INFORMATION_EX_StartingOffset: VarHandle = PARTITION_INFORMATION_EX.varHandle(
	groupElement("StartingOffset")
)
val PARTITION_INFORMATION_EX_PartitionLength: VarHandle = PARTITION_INFORMATION_EX.varHandle(
	groupElement("PartitionLength")
)
val PARTITION_INFORMATION_EX_PartitionNumber: VarHandle = PARTITION_INFORMATION_EX.varHandle(
	groupElement("PartitionNumber")
)
val PARTITION_INFORMATION_EX_RewritePartition: VarHandle = PARTITION_INFORMATION_EX.varHandle(
	groupElement("RewritePartition")
)
val PARTITION_INFORMATION_EX_IsServicePartition: VarHandle = PARTITION_INFORMATION_EX.varHandle(
	groupElement("IsServicePartition")
)
val PARTITION_INFORMATION_EX_DUMMYUNIONNAME: Long = PARTITION_INFORMATION_EX.byteOffset(
	groupElement("DUMMYUNIONNAME")
)