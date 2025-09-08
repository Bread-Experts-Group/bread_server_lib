package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.FeatureImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowNameFeature
import org.bread_experts_group.ffi.windows.stringToPCWSTR
import org.bread_experts_group.ffi.windows.wPCWSTRToString
import java.lang.foreign.Arena

class WindowsGraphicsWindowNameFeature(private val window: WindowsGraphicsWindow) : GraphicsWindowNameFeature() {
	override val expresses: FeatureExpression<GraphicsWindowNameFeature> = GraphicsWindowFeatures.WINDOW_NAME
	override val source: FeatureImplementationSource = FeatureImplementationSource.SYSTEM_NATIVE

	override var name: String
		get() {
			Arena.ofConfined().use { arena ->
				val buffer = arena.allocate(32767 * 2)
				window.sendMessage(
					0x000D, // WM_GETTEXT
					32767,
					buffer.address()
				)
				return wPCWSTRToString(buffer)
			}
		}
		set(value) {
			Arena.ofConfined().use { arena ->
				window.sendMessage(
					0x000C, // WM_SETTEXT
					0,
					stringToPCWSTR(arena, value).address()
				)
			}
		}
}