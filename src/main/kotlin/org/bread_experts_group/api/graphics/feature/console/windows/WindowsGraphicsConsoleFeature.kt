package org.bread_experts_group.api.graphics.feature.console.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.console.GraphicsConsoleFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.nativeAllocConsoleWithOptions
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

class WindowsGraphicsConsoleFeature : GraphicsConsoleFeature() {
	override val expresses: FeatureExpression<GraphicsConsoleFeature> = GraphicsFeatures.CUI_CONSOLE
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	override fun supported(): Boolean {
		val result = (nativeAllocConsoleWithOptions ?: return false).invokeExact(
			capturedStateSegment,
			MemorySegment.NULL,
			threadLocalDWORD0
		) as Int
		if (result != 0) throwLastError()
		val consoleStatus = threadLocalDWORD0.get(DWORD, 0)
		return consoleStatus != 0
	}
}