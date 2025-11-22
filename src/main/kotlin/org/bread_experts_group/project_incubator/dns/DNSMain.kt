package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.copy.WindowsCopySystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.copy.CopyHandleFeatures
import org.bread_experts_group.api.system.device.feature.copy.feature.routine.CopyProgressRoutineFeatures

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
		.get(SystemDeviceFeatures.APPEND).append("resources")
		.get(SystemDeviceFeatures.APPEND).append("app.asar")

	val copyTestFile = testFile.get(SystemDeviceFeatures.COPY).copy(
		testFile
			.get(SystemDeviceFeatures.APPEND).append("..")
			.get(SystemDeviceFeatures.APPEND).append("app.asar2")
	)
	copyTestFile.get(CopyHandleFeatures.COPY_PROGRESS_ROUTINE).routine = {
		val tfb = it.getOrNull(CopyProgressRoutineFeatures.TOTAL_SIZE_BYTES)
		val ttb = it.getOrNull(CopyProgressRoutineFeatures.TOTAL_TRANSFERRED_BYTES)
		if (tfb != null && ttb != null) println("${ttb.bytes} / ${tfb.bytes} (${(ttb.bytes.toDouble() / tfb.bytes) * 100}%)")
		listOf()
	}
	copyTestFile.start(
		WindowsCopySystemDeviceFeatures.DISABLE_SYSTEM_CACHE,
		WindowsCopySystemDeviceFeatures.DISABLE_PRE_ALLOCATION
	)
//	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}