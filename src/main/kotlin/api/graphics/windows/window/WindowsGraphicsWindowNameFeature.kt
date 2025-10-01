package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.PreInitializableClosable
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowNameFeature
import org.bread_experts_group.ffi.windows.WindowsMessageTypes
import java.lang.foreign.Arena

class WindowsGraphicsWindowNameFeature(private val window: WindowsGraphicsWindow) : GraphicsWindowNameFeature(),
	PreInitializableClosable {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	private var internalName: String? = "BSL Window"

	override fun open() {
		internalName = null
	}

	override fun close() {}

	override var name: String
		get() {
			return internalName ?: Arena.ofConfined().use { arena ->
				val textLength = window.sendMessage(
					WindowsMessageTypes.WM_GETTEXTLENGTH,
					0,
					0
				) + 1L
				val buffer = arena.allocate(textLength * 2)
				window.sendMessage(
					WindowsMessageTypes.WM_GETTEXT,
					textLength,
					buffer.address()
				)
				buffer.getString(0, Charsets.UTF_16LE)
			}
		}
		set(value) {
			if (internalName != null) internalName = value
			else Arena.ofConfined().use { arena ->
				window.sendMessage(
					WindowsMessageTypes.WM_SETTEXT,
					0,
					arena.allocateFrom(value, Charsets.UTF_16LE).address()
				)
			}
		}
}