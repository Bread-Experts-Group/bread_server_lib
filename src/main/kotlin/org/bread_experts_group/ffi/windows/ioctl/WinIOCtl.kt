package org.bread_experts_group.ffi.windows.ioctl

import org.bread_experts_group.ffi.windows.BYTE
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.LARGE_INTEGER
import java.lang.foreign.MemoryLayout
import java.lang.foreign.MemoryLayout.PathElement.groupElement
import java.lang.foreign.StructLayout
import java.lang.invoke.MethodHandle
import java.lang.invoke.VarHandle

/*
#define CTL_CODE( DeviceType, Function, Method, Access ) (                 \
    ((DeviceType) << 16) | ((Access) << 14) | ((Function) << 2) | (Method) \
)
 */

@Suppress("FunctionName")
fun CTL_CODE(deviceType: Int, function: Int, method: Int, access: Int): Int =
	(deviceType shl 16) or (access shl 14) or (function shl 2) or method

const val FILE_DEVICE_DISK = 0x00000007
const val FILE_DEVICE_FILE_SYSTEM = 0x00000009
const val FILE_DEVICE_MASS_STORAGE = 0x0000002D

const val METHOD_BUFFERED = 0
const val METHOD_NEITHER = 3

const val FILE_ANY_ACCESS = 0

const val IOCTL_DISK_BASE = FILE_DEVICE_DISK

val FSCTL_ALLOW_EXTENDED_DASD_IO = CTL_CODE(FILE_DEVICE_FILE_SYSTEM, 32, METHOD_NEITHER, FILE_ANY_ACCESS)
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