package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.io.open.StandardIOOpenFeatures

fun main() {
	val testFile = SystemProvider
		.get(SystemFeatures.GET_TEMPORARY_STORAGE_PATH_DEVICE).device
	testFile.get(SystemDeviceFeatures.IO_DEVICE).open(
		StandardIOOpenFeatures.CREATE,
		StandardIOOpenFeatures.DIRECTORY
	)
	testFile.get(SystemDeviceFeatures.PATH_CHILDREN).forEach {
		println(it.get(SystemDeviceFeatures.SYSTEM_IDENTIFIER).identity)
	}
//	println(testFile.get(SystemDeviceFeatures.PATH_GET_LAST_WRITE_TIME).getTime(StandardIOOpenFeatures.DIRECTORY))
//	println(testFile.get(SystemDeviceFeatures.PATH_GET_LAST_ACCESS_TIME).getTime(StandardIOOpenFeatures.DIRECTORY))
//	println(
//		testFile.get(SystemDeviceFeatures.PATH_GET_LAST_METADATA_WRITE_TIME).getTime(StandardIOOpenFeatures.DIRECTORY)
//	)
//	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}