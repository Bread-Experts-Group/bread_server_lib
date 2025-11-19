package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.StandardIOOpenFeatures

fun main() {
	val cwf = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("..")
		.get(SystemDeviceFeatures.APPEND).append("Windows")
		.get(SystemDeviceFeatures.APPEND).append("System32")
	println(cwf)
	println(cwf.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	println(cwf.getOrNull(SystemDeviceFeatures.FRIENDLY_NAME)?.name ?: "<no friendly name>")
	println(
		cwf.get(SystemDeviceFeatures.IO_DEVICE).open(
			StandardIOOpenFeatures.DIRECTORY,
			StandardIOOpenFeatures.CREATE
		)
	)
	println(cwf.get(SystemDeviceFeatures.PARENT).parent.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	println(cwf.get(SystemDeviceFeatures.CHILDREN).toList())
	cwf.get(SystemDeviceFeatures.CHILDREN).forEach {
		println(it.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	}
	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}