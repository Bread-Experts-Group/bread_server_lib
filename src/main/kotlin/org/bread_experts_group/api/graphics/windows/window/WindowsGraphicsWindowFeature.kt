package org.bread_experts_group.api.graphics.windows.window

import org.bread_experts_group.api.FeatureExpression
import org.bread_experts_group.api.ImplementationSource
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindow
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowFeature
import org.bread_experts_group.api.graphics.feature.window.GraphicsWindowTemplate
import org.bread_experts_group.ffi.windows.*
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindowFeature : GraphicsWindowFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override val expresses: FeatureExpression<GraphicsWindowFeature> = GraphicsFeatures.WINDOW
	override fun supported(): Boolean = Arena.ofConfined().use { arena ->
		if (
			nativeOpenWindowStationW == null ||
			nativeGetProcessWindowStation == null ||
			nativeSetProcessWindowStation == null ||
			nativeOpenDesktopW == null ||
			nativeCloseWindowStation == null ||
			nativeCloseDesktop == null
		) return@use false
		val station = nativeOpenWindowStationW.invokeExact(
			arena.allocateFrom("WinSta0", Charsets.UTF_16LE),
			0,
			WindowStationAccessRights.READ_CONTROL.position.toInt()
		) as MemorySegment
		if (station == MemorySegment.NULL) return false
		val savedStation = nativeGetProcessWindowStation.invokeExact() as MemorySegment
		val setStationCheck = nativeSetProcessWindowStation.invokeExact(station) as Int
		if (setStationCheck == 0) return false
		val stationDesktop = nativeOpenDesktopW.invokeExact(
			arena.allocateFrom("Default", Charsets.UTF_16LE),
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

	override fun createTemplate(): GraphicsWindowTemplate = WindowsGraphicsWindowTemplate()
	override fun createWindow(template: GraphicsWindowTemplate): GraphicsWindow = WindowsGraphicsWindow(
		template as WindowsGraphicsWindowTemplate
	)
}