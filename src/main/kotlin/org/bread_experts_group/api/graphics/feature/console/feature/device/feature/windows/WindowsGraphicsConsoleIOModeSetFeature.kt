package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOModeSetFeature
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleModes
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.nativeSetConsoleMode
import org.bread_experts_group.ffi.windows.throwLastError
import java.lang.foreign.MemorySegment
import java.util.*

class WindowsGraphicsConsoleIOModeSetFeature(
	private val handle: MemorySegment,
	private val mapping: Map<GraphicsConsoleModes, UInt>,
	private val input: Boolean
) : GraphicsConsoleIOModeSetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	// TODO: figure out if there is a way to detect this before exec
	override fun supported(): Boolean = nativeSetConsoleMode != null

	override fun setMode(set: EnumSet<GraphicsConsoleModes>) {
		val status = nativeSetConsoleMode!!.invokeExact(
			capturedStateSegment,
			handle,
			set.fold(if (input) 0x80 else 0) { a, r -> a or (mapping[r]!!.toInt()) }
		) as Int
		if (status == 0) throwLastError()
	}
}