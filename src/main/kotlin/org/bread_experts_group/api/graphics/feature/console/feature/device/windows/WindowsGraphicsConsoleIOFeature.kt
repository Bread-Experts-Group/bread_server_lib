package org.bread_experts_group.api.graphics.feature.console.feature.device.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleIOFeature
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleModes
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows.*
import org.bread_experts_group.api.system.io.windows.WindowsIODevice
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.INVALID_HANDLE_VALUE
import org.bread_experts_group.ffi.windows.nativeGetStdHandle
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment

abstract class WindowsGraphicsConsoleIOFeature(
	override val expresses: FeatureExpression<GraphicsConsoleIOFeature>,
	private val stdHandle: UInt,
	private val input: Boolean
) : GraphicsConsoleIOFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	override fun supported(): Boolean {
		val stdHandle = (nativeGetStdHandle ?: return false).invokeExact(
			capturedStateSegment,
			stdHandle.toInt()
		) as MemorySegment
		if (stdHandle == INVALID_HANDLE_VALUE) throwLastError()
		features.add(WindowsGraphicsConsoleIODeviceGetFeature(WindowsIODevice(stdHandle)))
		val mapping = if (input) mapOf(
			0x0001u to GraphicsConsoleModes.INPUT_SYSTEM_PROCESSED,
			0x0002u to GraphicsConsoleModes.INPUT_LINE_BLOCK,
			0x0004u to GraphicsConsoleModes.INPUT_ECHO_INPUT,
			0x0008u to GraphicsConsoleModes.INPUT_WINDOW_EVENTS,
			0x0010u to GraphicsConsoleModes.INPUT_MOUSE_EVENTS,
			0x0020u to GraphicsConsoleModes.INPUT_INSERT_MODE,
			0x0200u to GraphicsConsoleModes.INPUT_CONTROL_SEQUENCES
		) else mapOf(
			0x0001u to GraphicsConsoleModes.OUTPUT_SYSTEM_PROCESSED,
			0x0004u to GraphicsConsoleModes.OUTPUT_CONTROL_SEQUENCES,
			0x0008u to GraphicsConsoleModes.OUTPUT_SCROLL_AT_EDGE
		)
		features.add(WindowsGraphicsConsoleIOModeGetFeature(stdHandle, mapping))
		features.add(
			WindowsGraphicsConsoleIOModeSetFeature(
				stdHandle, mapping.entries.associateBy({ it.value }) { it.key }, input
			)
		)
		features.add(WindowsGraphicsConsoleIOGetCodePageFeature(input))
		features.add(WindowsGraphicsConsoleIOSetCodePageFeature(input))
		features.add(WindowsGraphicsConsoleIOEventGetFeature(stdHandle))
		return true
	}
}