package org.bread_experts_group.project_incubator.bpdi

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.firmware_info.IODeviceFirmwareSlotReadOnly
import org.bread_experts_group.api.system.io.firmware_info.IODeviceFirmwareSlotRevision
import org.bread_experts_group.api.system.io.firmware_info.IODeviceFirmwareSlots
import org.bread_experts_group.api.system.io.geometry.IODeviceGeometryDeviceBytesPerSectorCount
import org.bread_experts_group.api.system.io.geometry.IODeviceGeometryDeviceCylinderCount
import org.bread_experts_group.api.system.io.geometry.IODeviceGeometryDeviceSectorsPerTrackCount
import org.bread_experts_group.api.system.io.geometry.IODeviceGeometryDeviceTracksPerCylinderCount
import org.bread_experts_group.api.system.io.open.FileIOOpenFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.StandardIOOpenFeatures
import org.bread_experts_group.api.system.io.size.DataSize
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.generic.io.reader.BSLWriter
import java.lang.foreign.ValueLayout

fun main() {
	val disk = SystemProvider.get(SystemFeatures.GET_PATH_DEVICE_DIRECT)
		.get("\\\\.\\PhysicalDrive4")
	val diskIO = disk.get(SystemDeviceFeatures.IO_DEVICE)
		.open(
			FileIOReOpenFeatures.READ,
			FileIOReOpenFeatures.SHARE_READ,
			FileIOReOpenFeatures.SHARE_WRITE
		)
		.firstNotNullOf { it as? IODevice }
	val output = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_PATH_DEVICE).device
		.get(SystemDeviceFeatures.PATH_APPEND).append("test.bpdi")
	val outputIO = output.get(SystemDeviceFeatures.IO_DEVICE)
		.open(
			StandardIOOpenFeatures.CREATE,
			FileIOOpenFeatures.TRUNCATE,
			FileIOReOpenFeatures.READ,
			FileIOReOpenFeatures.WRITE,
			FileIOReOpenFeatures.SHARE_READ
		)
		.firstNotNullOf { it as? IODevice }
	val diskSize = diskIO.get(IODeviceFeatures.GET_SIZE).get()
		.firstNotNullOf { it as? DataSize }.bytes
	val diskGeometry = diskIO.get(IODeviceFeatures.GET_DEVICE_GEOMETRY).get()
	val diskFirmware = diskIO.get(IODeviceFeatures.GET_DEVICE_FIRMWARE_INFO).get()
	val fileOutput = autoArena.allocate(512)
	val fileWriter = BSLWriter(outputIO.get(IODeviceFeatures.WRITE))
	fileWriter.write8(0)
	var flags = 0
	val diskGeometryCylinderCount = diskGeometry.firstNotNullOfOrNull { it as? IODeviceGeometryDeviceCylinderCount }
	val diskGeometryTrackCount = if (diskGeometryCylinderCount != null)
		diskGeometry.firstNotNullOfOrNull { it as? IODeviceGeometryDeviceTracksPerCylinderCount }
	else null
	val diskGeometrySectorCount = if (diskGeometryTrackCount != null)
		diskGeometry.firstNotNullOfOrNull { it as? IODeviceGeometryDeviceSectorsPerTrackCount }
	else null
	val diskGeometryByteCount = if (diskGeometrySectorCount != null)
		diskGeometry.firstNotNullOfOrNull { it as? IODeviceGeometryDeviceBytesPerSectorCount }
	else null
	if (diskGeometryByteCount != null) flags = flags or 0b00000001
	if (diskFirmware.isNotEmpty()) flags = flags or 0b00000010
	fileWriter.write8i(flags)
	if (diskGeometryByteCount != null) {
		// TODO: wherever longs are used, use the spec'd extensible integer
		fileWriter.write64(diskGeometryCylinderCount!!.cylinders)
		fileWriter.write64(diskGeometryTrackCount!!.tracks.toLong())
		fileWriter.write64(diskGeometrySectorCount!!.sectors.toLong())
	}
	if (diskFirmware.isNotEmpty()) {
		fileWriter.write8(0)
		val slots = diskFirmware.firstNotNullOfOrNull { it as? IODeviceFirmwareSlots }?.slots
		if (!slots.isNullOrEmpty()) {
			fileWriter.write64(slots.size.toLong())
			for ((number, slot) in slots) {
				fileWriter.write64(number.toLong())
				var flags = 0
				if (slot.firstNotNullOfOrNull { it as? IODeviceFirmwareSlotReadOnly }?.readOnly == true)
					flags = flags or 0b00000001
				val revisionData = slot.firstNotNullOfOrNull { it as? IODeviceFirmwareSlotRevision }
				if (revisionData != null) flags = flags or 0b00000010
				fileWriter.write8i(flags)
				if (revisionData != null) {
					fileWriter.write64(revisionData.revision.size.toLong())
					fileWriter.write(revisionData.revision)
				}
			}
		} else fileWriter.write64(0)
	}
	fileWriter.write64((diskGeometryByteCount?.bytes ?: 512).toLong())
	fileWriter.write64(diskSize)
	fileWriter.flush()
	TODO("!")

	val data = autoArena.allocate(1024)
	println(diskIO.get(IODeviceFeatures.READ).receiveSegment(data).block())
	println(data.toArray(ValueLayout.JAVA_BYTE).toHexString())
//	diskIO.get(IODeviceFeatures.READ).receiveSegment(data)
//	println(data.toArray(ValueLayout.JAVA_BYTE).toHexString())
}