package org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.windows

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowDisplayAffinityFeature
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinityGetData
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinityGetFeature
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinitySetData
import org.bread_experts_group.api.graphics.feature.window.feature.display_affinity.GraphicsWindowDisplayAffinitySetFeature
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindowDisplayAffinityFeature(private val hWnd: MemorySegment) :
	GraphicsWindowDisplayAffinityFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun get(
		vararg features: GraphicsWindowDisplayAffinityGetFeature
	): List<GraphicsWindowDisplayAffinityGetData> {
		val status = nativeGetWindowDisplayAffinity!!.invokeExact(
			capturedStateSegment,
			this.hWnd,
			threadLocalDWORD0
		) as Int
		if (status == 0) throwLastError()
		return when (threadLocalDWORD0.get(DWORD, 0)) {
			WDA_NONE -> listOf(WindowsDisplayAffinityStatus.NO_RESTRICTION)
			WDA_MONITOR -> listOf(WindowsDisplayAffinityStatus.MONITOR_ONLY_CONTENT)
			WDA_EXCLUDEFROMCAPTURE -> listOf(WindowsDisplayAffinityStatus.MONITOR_ONLY_WINDOW)
			else -> emptyList()
		}
	}

	override fun set(
		vararg features: GraphicsWindowDisplayAffinitySetFeature
	): List<GraphicsWindowDisplayAffinitySetData> {
		val (affinity, selected) = if (features.contains(WindowsDisplayAffinityStatus.MONITOR_ONLY_WINDOW))
			WDA_EXCLUDEFROMCAPTURE to WindowsDisplayAffinityStatus.MONITOR_ONLY_WINDOW
		else if (features.contains(WindowsDisplayAffinityStatus.MONITOR_ONLY_CONTENT))
			WDA_MONITOR to WindowsDisplayAffinityStatus.MONITOR_ONLY_CONTENT
		else if (features.contains(WindowsDisplayAffinityStatus.NO_RESTRICTION))
			WDA_NONE to WindowsDisplayAffinityStatus.NO_RESTRICTION
		else return emptyList()
		val status = nativeSetWindowDisplayAffinity!!.invokeExact(
			capturedStateSegment,
			this.hWnd,
			affinity
		) as Int
		if (status == 0) throwLastError()
		return listOf(selected)
	}

	companion object {
		const val WDA_NONE: Int = 0x00000000
		const val WDA_MONITOR: Int = 0x00000001
		const val WDA_EXCLUDEFROMCAPTURE: Int = 0x00000011
	}
}