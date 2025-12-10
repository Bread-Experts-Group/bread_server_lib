package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.coding.codepage.CodePageDataIdentifier
import org.bread_experts_group.api.coding.codepage.windows.WindowsCodePageDataIdentifier
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOSetCodePageFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeSetConsoleCP
import org.bread_experts_group.ffi.windows.nativeSetConsoleOutputCP
import org.bread_experts_group.ffi.windows.throwLastError

class WindowsGraphicsConsoleIOSetCodePageFeature(
	private val input: Boolean
) : GraphicsConsoleIOSetCodePageFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	override fun supported(): Boolean {
		return !(nativeSetConsoleCP == null || nativeSetConsoleOutputCP == null)
	}

	override fun setCodePage(codePage: CodePageDataIdentifier) {
		codePage as WindowsCodePageDataIdentifier
		val status = if (input) nativeSetConsoleCP!!.invokeExact(
			capturedStateSegment,
			codePage.raw.toInt()
		) as Int else nativeSetConsoleOutputCP!!.invokeExact(
			capturedStateSegment,
			codePage.raw.toInt()
		) as Int
		if (status == 0) throwLastError()
	}
}