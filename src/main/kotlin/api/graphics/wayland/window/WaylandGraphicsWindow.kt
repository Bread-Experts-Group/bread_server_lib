package org.bread_experts_group.api.graphics.wayland.window

import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatureImplementation

class WaylandGraphicsWindow(template: WaylandGraphicsWindowTemplate) : GraphicsWindow() {
	override val features: Set<GraphicsWindowFeatureImplementation<*>> = TODO()

	init {
		println("I haven't coded this part yet, but I know you're using wayland")
	}
}