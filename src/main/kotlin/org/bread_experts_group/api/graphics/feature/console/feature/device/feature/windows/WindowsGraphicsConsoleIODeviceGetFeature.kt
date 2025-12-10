package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIODeviceGetFeature

class WindowsGraphicsConsoleIODeviceGetFeature(override val device: org.bread_experts_group.api.system.io.IODevice) :
	GraphicsConsoleIODeviceGetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true
}