package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

fun main() {
	val testFile = SystemProvider
		.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("Program Files (x86)")
		.get(SystemDeviceFeatures.APPEND).append("Steam")
		.get(SystemDeviceFeatures.APPEND).append("steamapps")
		.get(SystemDeviceFeatures.APPEND).append("common")
		.get(SystemDeviceFeatures.APPEND).append("でびるコネクショん")

	fun SystemDevice.encrypt() {
		this.get(SystemDeviceFeatures.DISABLE_TRANSPARENT_ENCRYPT).disable()
		this.get(SystemDeviceFeatures.CHILDREN).forEach { it.encrypt() }
	}

	testFile.encrypt()
//	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}