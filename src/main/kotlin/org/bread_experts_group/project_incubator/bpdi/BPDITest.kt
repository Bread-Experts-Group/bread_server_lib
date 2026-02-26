package org.bread_experts_group.project_incubator.bpdi

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.ffi.autoArena
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
	val diskGeometry = diskIO.get(IODeviceFeatures.GET_DEVICE_GEOMETRY).get()
	val diskFirmware = diskIO.get(IODeviceFeatures.GET_DEVICE_FIRMWARE_INFO).get()
	println(diskGeometry)
	println(diskFirmware)
	val data = autoArena.allocate(512)
	diskIO.get(IODeviceFeatures.READ).receiveSegment(data)
	println(data.toArray(ValueLayout.JAVA_BYTE).toHexString())
	diskIO.get(IODeviceFeatures.READ).receiveSegment(data)
	println(data.toArray(ValueLayout.JAVA_BYTE).toHexString())
}