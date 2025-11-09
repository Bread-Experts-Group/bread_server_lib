package org.bread_experts_group.api.graphics.feature.window.wayland

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowFeature
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowTemplate
import org.bread_experts_group.ffi.wayland.nativeWLDisplayConnect
import org.bread_experts_group.ffi.wayland.nativeWLDisplayDisconnect
import java.lang.foreign.MemorySegment

class WaylandGraphicsWindowFeature : GraphicsWindowFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override val expresses: FeatureExpression<GraphicsWindowFeature> = GraphicsFeatures.GUI_WINDOW
	override fun createTemplate(): GraphicsWindowTemplate = WaylandGraphicsWindowTemplate()
	override fun createWindow(template: GraphicsWindowTemplate): GraphicsWindow = WaylandGraphicsWindow(
		template as WaylandGraphicsWindowTemplate
	)

	override fun supported(): Boolean {
		val display = (nativeWLDisplayConnect ?: return false).invokeExact(MemorySegment.NULL) as MemorySegment
		if (display == MemorySegment.NULL) return false
		(nativeWLDisplayDisconnect ?: return false).invokeExact(display)
		return true
	}
}