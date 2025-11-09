package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.coding.CodingFormat
import org.bread_experts_group.api.coding.windows.WindowsCodingFormat
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOCodingGetFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeGetConsoleCP
import org.bread_experts_group.ffi.windows.nativeGetConsoleOutputCP

class WindowsGraphicsConsoleIOCodingGetFeature(
	private val input: Boolean
) : GraphicsConsoleIOCodingGetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	private fun getCodePage(): UInt {
		val page = if (input) nativeGetConsoleCP!!.invokeExact(capturedStateSegment) as Int
		else nativeGetConsoleOutputCP!!.invokeExact(capturedStateSegment) as Int
		if (page == 0) decodeLastError()
		return page.toUInt()
	}

	override fun supported(): Boolean {
		if (nativeGetConsoleCP == null || nativeGetConsoleOutputCP == null) return false
		getCodePage()
		return true
	}

	override val coding: CodingFormat
		get() = WindowsCodingFormat(getCodePage())
}