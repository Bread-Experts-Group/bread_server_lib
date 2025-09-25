package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowRenderEventFeature

class WindowsGraphicsWindowRenderEventFeature : GraphicsWindowRenderEventFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
}