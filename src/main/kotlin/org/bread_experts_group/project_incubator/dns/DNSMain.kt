package org.bread_experts_group.project_incubator.dns

import org.bread_experts_group.api.system.SystemFeatures
import org.bread_experts_group.api.system.SystemProvider
import org.bread_experts_group.api.system.device.SystemDeviceFeatures
import org.bread_experts_group.api.system.device.feature.move.MoveHandleFeatures
import org.bread_experts_group.api.system.device.feature.move.feature.routine.MoveProgressRoutineFeatures
import org.bread_experts_group.api.system.device.move.WindowsMoveSystemDeviceFeatures

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

	val moveTestFile = testFile.get(SystemDeviceFeatures.MOVE).move(
		testFile
			.get(SystemDeviceFeatures.APPEND).append("..")
			.get(SystemDeviceFeatures.APPEND).append("app.asar2")
	)
	moveTestFile.get(MoveHandleFeatures.MOVE_PROGRESS_ROUTINE).routine = {
		val tfb = it.getOrNull(MoveProgressRoutineFeatures.TOTAL_SIZE_BYTES)
		val ttb = it.getOrNull(MoveProgressRoutineFeatures.TOTAL_TRANSFERRED_BYTES)
		if (tfb != null && ttb != null) println("${ttb.bytes} / ${tfb.bytes} (${(ttb.bytes.toDouble() / tfb.bytes) * 100}%)")
		listOf()
	}
	moveTestFile.start(
		WindowsMoveSystemDeviceFeatures.WRITE_THROUGH
	)
//	val pfh = SystemProvider.get(SystemFeatures.PROJECTED_FILE_HIERARCHY)
}