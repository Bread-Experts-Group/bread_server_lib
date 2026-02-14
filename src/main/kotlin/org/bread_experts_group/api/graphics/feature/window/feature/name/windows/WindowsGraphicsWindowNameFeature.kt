package org.bread_experts_group.api.graphics.feature.window.feature.name.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowNameFeature
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameGetData
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameGetFeature
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameSetData
import org.bread_experts_group.api.graphics.feature.window.feature.name.GraphicsWindowNameSetFeature
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowName
import org.bread_experts_group.api.graphics.feature.window.windows.WindowsGraphicsWindow
import org.bread_experts_group.ffi.windows.WCHAR
import org.bread_experts_group.ffi.windows.WindowsWindowMessages
import java.lang.foreign.Arena

class WindowsGraphicsWindowNameFeature(private val window: WindowsGraphicsWindow) : GraphicsWindowNameFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun get(vararg features: GraphicsWindowNameGetFeature): List<GraphicsWindowNameGetData> {
		val length = window.sendMessage(WindowsWindowMessages.WM_GETTEXTLENGTH.id, 0, 0) + 1
		val text = Arena.ofConfined().use { textArena ->
			val buffer = textArena.allocate(WCHAR, length)
			val copied = window.sendMessage(
				WindowsWindowMessages.WM_GETTEXT.id,
				length, buffer.address()
			)
			if (length - 1 != copied) TODO("Err")
			buffer.getString(0, Charsets.UTF_16LE)
		}
		return listOf(GraphicsWindowName(text))
	}

	override fun set(vararg features: GraphicsWindowNameSetFeature): List<GraphicsWindowNameSetData> {
		val newName = features.firstNotNullOfOrNull { it as? GraphicsWindowName } ?: return emptyList()
		Arena.ofConfined().use { textArena ->
			window.sendMessage(
				WindowsWindowMessages.WM_SETTEXT.id,
				0, textArena.allocateFrom(newName.name, Charsets.UTF_16LE).address()
			)
		}
		return listOf(newName)
	}
}