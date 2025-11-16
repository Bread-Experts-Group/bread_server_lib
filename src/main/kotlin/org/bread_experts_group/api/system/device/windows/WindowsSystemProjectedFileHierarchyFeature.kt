package org.bread_experts_group.api.system.device.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.system.device.io.IODevice
import org.bread_experts_group.api.system.feature.SystemProjectedFileHierarchyFeature

class WindowsSystemProjectedFileHierarchyFeature : SystemProjectedFileHierarchyFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true // TODO PFHF
	override fun open(into: IODevice) {
		TODO("Not yet implemented")
	}
}