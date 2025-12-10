package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier
import org.bread_experts_group.api.coding.codepage.windows.WindowsCodePage
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOGetCodePageFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeGetConsoleCP
import org.bread_experts_group.ffi.windows.nativeGetConsoleOutputCP
import org.bread_experts_group.ffi.windows.throwLastError

class WindowsGraphicsConsoleIOGetCodePageFeature(
	private val input: Boolean
) : GraphicsConsoleIOGetCodePageFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private fun getCodePage(): UInt {
		val page = if (input) nativeGetConsoleCP!!.invokeExact(capturedStateSegment) as Int
		else nativeGetConsoleOutputCP!!.invokeExact(capturedStateSegment) as Int
		if (page == 0) throwLastError()
		return page.toUInt()
	}

	override fun supported(): Boolean {
		if (nativeGetConsoleCP == null || nativeGetConsoleOutputCP == null) return false
		getCodePage()
		return true
	}

	override val codePage: CodePageDataIdentifier
		get() = WindowsCodePage(getCodePage())
}