package org.bread_experts_group.api.system.io.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.io.feature.IODeviceGetDeviceGeometryFeature
import org.bread_experts_group.api.system.io.geometry.*
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.ioctl.*
import org.bread_experts_group.ffi.windows.nativeDeviceIoControl
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsIOGetDeviceGeometryFeature(
	private val handle: MemorySegment
) : IODeviceGetDeviceGeometryFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = nativeDeviceIoControl != null

	override fun get(vararg features: IODeviceGetGeometryFeatureIdentifier): List<IODeviceGetGeometryDataIdentifier> {
		val data = mutableListOf<IODeviceGetGeometryDataIdentifier>()
		val buffer = autoArena.allocate(DISK_GEOMETRY_EX)
		val status = nativeDeviceIoControl!!.invokeExact(
			capturedStateSegment,
			handle,
			IOCTL_DISK_GET_DRIVE_GEOMETRY_EX,
			MemorySegment.NULL,
			0,
			buffer,
			buffer.byteSize().toInt(),
			threadLocalDWORD0,
			MemorySegment.NULL
		) as Int
		if (status == 0) throwLastError()
		val geometry = DISK_GEOMETRY_EX_Geometry.invokeExact(buffer, 0L) as MemorySegment
		data.add(IODeviceGeometryDeviceCylinderCount(DISK_GEOMETRY_Cylinders.get(geometry, 0) as Long))
		data.add(IODeviceGeometryDeviceTracksPerCylinderCount(DISK_GEOMETRY_TracksPerCylinder.get(geometry, 0) as Int))
		data.add(IODeviceGeometryDeviceSectorsPerTrackCount(DISK_GEOMETRY_SectorsPerTrack.get(geometry, 0) as Int))
		data.add(IODeviceGeometryDeviceBytesPerSectorCount(DISK_GEOMETRY_BytesPerSector.get(geometry, 0) as Int))
		data.add(IODeviceGeometryDeviceSize(DISK_GEOMETRY_EX_DiskSize.get(buffer, 0) as Long))
		return data
	}
}