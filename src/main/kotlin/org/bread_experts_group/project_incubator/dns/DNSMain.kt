package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import org.bread_experts_group.api.system.device.io.open.FileIOOpenFeatures
import org.bread_experts_group.api.system.device.io.open.StandardIOOpenFeatures
import org.bread_experts_group.api.system.device.io.open.WindowsIOOpenFeatures

fun main() {
	val testFile = SystemProvider
		.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
		.get(SystemDeviceFeatures.APPEND).append("re_open.kts")
		.get(SystemDeviceFeatures.IO_DEVICE).open(
			StandardIOOpenFeatures.CREATE
		)!!.first.get(IODeviceFeatures.REOPEN).reopen(
			FileIOOpenFeatures.WRITE,
			WindowsIOOpenFeatures.WRITE_THROUGH
		)!!.first.get(IODeviceFeatures.WRITE).write(
			"Meow",
			Charsets.UTF_8
		)
	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}