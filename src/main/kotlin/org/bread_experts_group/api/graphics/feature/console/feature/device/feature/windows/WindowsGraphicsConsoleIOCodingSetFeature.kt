package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.coding.CodingFormat
import org.bread_experts_group.api.coding.windows.WindowsCodingFormat
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOCodingSetFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeSetConsoleCP
import org.bread_experts_group.ffi.windows.nativeSetConsoleOutputCP

class WindowsGraphicsConsoleIOCodingSetFeature(
	private val input: Boolean
) : GraphicsConsoleIOCodingSetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	override fun supported(): Boolean {
		return !(nativeSetConsoleCP == null || nativeSetConsoleOutputCP == null)
	}

	override fun setCoding(coding: CodingFormat) {
		coding as WindowsCodingFormat
		val status = if (input) nativeSetConsoleCP!!.invokeExact(
			capturedStateSegment,
			coding.pageNr.toInt()
		) as Int else nativeSetConsoleOutputCP!!.invokeExact(
			capturedStateSegment,
			coding.pageNr.toInt()
		) as Int
		if (status == 0) decodeLastError()
	}
}