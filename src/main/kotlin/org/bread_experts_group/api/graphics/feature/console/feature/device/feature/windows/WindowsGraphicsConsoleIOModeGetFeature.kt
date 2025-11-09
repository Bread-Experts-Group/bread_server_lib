package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.windows

import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleIOModeGetFeature
import org.bread_experts_group.api.graphics.feature.console.feature.device.feature.GraphicsConsoleModes
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.DWORD
import org.bread_experts_group.ffi.windows.decodeLastError
import org.bread_experts_group.ffi.windows.nativeGetConsoleMode
import org.bread_experts_group.ffi.windows.threadLocalDWORD0
import java.lang.foreign.MemorySegment
import java.util.*

class WindowsGraphicsConsoleIOModeGetFeature(
	private val handle: MemorySegment,
	private val mapping: Map<UInt, GraphicsConsoleModes>
) : GraphicsConsoleIOModeGetFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	private fun getMode(): Pair<Int, UInt> {
		val status = nativeGetConsoleMode!!.invokeExact(
			capturedStateSegment,
			handle,
			threadLocalDWORD0
		) as Int
		return status to threadLocalDWORD0.get(DWORD, 0).toUInt()
	}

	override fun supported(): Boolean {
		if (nativeGetConsoleMode == null) return false
		val (status, _) = getMode()
		return status != 0
	}

	override val mode: EnumSet<GraphicsConsoleModes>
		get() {
			val (status, mode) = getMode()
			if (status == 0) decodeLastError()
			val modes = EnumSet.noneOf(GraphicsConsoleModes::class.java)
			mapping.forEach { (k, v) -> if ((mode and k) > 0u) modes.add(v) }
			return modes
		}
}