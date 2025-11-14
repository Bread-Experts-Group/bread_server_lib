package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIODeviceGetFeature
import org.bread_experts_group.api.system.device.io.IODevice

class WindowsGraphicsConsoleIODeviceGetFeature(override val device: IODevice) : GraphicsConsoleIODeviceGetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun supported(): Boolean = true
}