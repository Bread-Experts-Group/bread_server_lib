package org.bread_experts_group.api.system.device.feature.copy.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.feature.copy.feature.SystemDeviceCopyProgressRoutineFeature

class WindowsSystemDeviceCopyProgressRoutineFeature : SystemDeviceCopyProgressRoutineFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}