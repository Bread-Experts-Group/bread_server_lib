package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures

fun main() {
	val cwd = SystemProvider.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
	println(cwd)
	println(cwd.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	println(cwd.get(SystemDeviceFeatures.FRIENDLY_NAME).name)
	println(cwd.get(SystemDeviceFeatures.IO_DEVICE).open())
	println(cwd.get(SystemDeviceFeatures.PARENT).parent)
	println(cwd.get(SystemDeviceFeatures.CHILDREN).toList())
	cwd.get(SystemDeviceFeatures.CHILDREN).forEach {
		println(it.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	}
	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}