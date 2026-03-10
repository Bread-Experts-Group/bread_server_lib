package org.bread_experts_group.project_incubator

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.io.IODevice
import org.bread_experts_group.api.system.io.IODeviceFeatures
import org.bread_experts_group.api.system.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.io.open.StandardIOOpenFeatures

fun main() {
	SystemProvider.get(SystemFeatures.GET_PATH_DEVICE_DIRECT).get("C:\\")
		.get(SystemDeviceFeatures.PATH_CHILDREN).forEach {
			try {
				println(it.type)
				println(it.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
				val ioStatus = it.get(SystemDeviceFeatures.IO_DEVICE).open(
					StandardIOOpenFeatures.DIRECTORY,
					FileIOReOpenFeatures.SHARE_READ,
					FileIOReOpenFeatures.SHARE_WRITE,
					FileIOReOpenFeatures.READ
				)
				val ioDevice = ioStatus.firstNotNullOf { it as? IODevice }
				println(ioDevice.getOrNull(IODeviceFeatures.GET_SIZE)?.get())
			} catch (e: Throwable) {
				e.printStackTrace()
			}
		}
}