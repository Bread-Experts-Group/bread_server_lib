package org.bread_experts_group.api.system.device.feature.move.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.move.feature.SystemDeviceMoveProgressRoutineFeature

class WindowsSystemDeviceMoveProgressRoutineFeature : SystemDeviceMoveProgressRoutineFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}