package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowResizeEventFeature

class WindowsGraphicsWindowResizeEventFeature : GraphicsWindowResizeEventFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}