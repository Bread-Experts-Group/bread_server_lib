package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.IODeviceFeatures
import org.bread_experts_group.api.system.device.io.open.FileIOReOpenFeatures
import org.bread_experts_group.api.system.device.io.open.StandardIOOpenFeatures

fun main() {
	val testFile = SystemProvider
		.get(SystemFeatures.GET_CURRENT_WORKING_DEVICE).device
		.get(SystemDeviceFeatures.APPEND).append("testFile.txt")
	testFile.get(SystemDeviceFeatures.IO_DEVICE).open(
		StandardIOOpenFeatures.CREATE,
		FileIOReOpenFeatures.WRITE
	)!!.first.also {
		it.get(IODeviceFeatures.WRITE).write("Test", Charsets.UTF_8)
		it.get(IODeviceFeatures.RELEASE).close()
	}
	testFile.get(SystemDeviceFeatures.ENABLE_TRANSPARENT_ENCRYPT).enable()
	testFile.get(SystemDeviceFeatures.CHILDREN_STREAMS).forEach {
		println(it.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	}
//	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}