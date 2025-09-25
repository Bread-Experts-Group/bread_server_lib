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
		val station = nativeOpenWindowStationW.invokeExact(
			stringToPCWSTR(arena, "WinSta0"),
			0,
			WindowStationAccessRights.READ_CONTROL.position.toInt()
		) as MemorySegment
		if (station == MemorySegment.NULL) return false
		val savedStation = nativeGetProcessWindowStation.invokeExact() as MemorySegment
		val setStationCheck = nativeSetProcessWindowStation.invokeExact(station) as Int
		if (setStationCheck == 0) return false
		val stationDesktop = nativeOpenDesktopW.invokeExact(
			stringToPCWSTR(arena, "Default"),
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