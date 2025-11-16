package org.bread_experts_group.api.graphics.feature.window.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowResizeEventFeature

class WindowsGraphicsWindowResizeEventFeature : GraphicsWindowResizeEventFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}