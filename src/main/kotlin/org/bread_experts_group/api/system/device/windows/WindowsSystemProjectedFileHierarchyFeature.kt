package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.system.device.SystemDevice
import org.bread_experts_group.api.system.feature.SystemProjectedFileHierarchyFeature

class WindowsSystemProjectedFileHierarchyFeature : SystemProjectedFileHierarchyFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true // TODO PFHF
	override fun open(into: SystemDevice) {
		TODO("Not yet implemented")
	}
}