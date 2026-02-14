package org.bread_experts_group.api.graphics.feature.window.windows

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowFeature
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowOpenDataIdentifier
import org.bread_experts_group.api.graphics.feature.window.open.GraphicsWindowOpenFeatureIdentifier
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindowFeature : GraphicsWindowFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override val expresses: FeatureExpression<GraphicsWindowFeature> = GraphicsFeatures.GUI_WINDOW
	override fun supported(): Boolean = Arena.ofConfined().use { arena ->
		if (
			nativeOpenWindowStationWide == null ||
			nativeGetProcessWindowStation == null ||
			nativeSetProcessWindowStation == null ||
			nativeOpenDesktopWide == null ||
			nativeCloseWindowStation == null ||
			nativeCloseDesktop == null
		) return@use false
		val station = nativeOpenWindowStationWide.invokeExact(
			arena.allocateFrom("WinSta0", winCharsetWide),
			0,
			WindowStationAccessRights.READ_CONTROL.position.toInt()
		) as MemorySegment
		if (station == MemorySegment.NULL) return false
		val savedStation = nativeGetProcessWindowStation.invokeExact() as MemorySegment
		val setStationCheck = nativeSetProcessWindowStation.invokeExact(station) as Int
		if (setStationCheck == 0) return false
		val stationDesktop = nativeOpenDesktopWide.invokeExact(
			arena.allocateFrom("Default", winCharsetWide),
			0,
			0,
			WindowStationAccessRights.READ_CONTROL.position.toInt()
		) as MemorySegment
		nativeSetProcessWindowStation.invokeExact(savedStation) as Int
		val stationDesktopOK = stationDesktop != MemorySegment.NULL
		nativeCloseWindowStation.invokeExact(station) as Int
		nativeCloseDesktop.invokeExact(stationDesktop) as Int
		return stationDesktopOK
	}

	override fun open(
		vararg features: GraphicsWindowOpenFeatureIdentifier
	): List<GraphicsWindowOpenDataIdentifier> {
		val data = mutableListOf<GraphicsWindowOpenDataIdentifier>()
		data.add(WindowsGraphicsWindow(data, *features))
		return data
	}
}