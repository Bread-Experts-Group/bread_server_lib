package org.bread_experts_group.api.graphics.feature.window.feature

import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.feature.window.feature.client_area_coordinates.GraphicsWindowClientAreaCoordinatesDataIdentifier
import org.bread_experts_group.api.graphics.feature.window.feature.client_area_coordinates.GraphicsWindowRectangleI
import org.bread_experts_group.ffi.autoArena
import org.bread_experts_group.ffi.capturedStateSegment
import org.bread_experts_group.ffi.windows.*
import org.bread_experts_group.generic.numeric.geometry.point.Point2
import java.lang.foreign.MemorySegment

class WindowsGraphicsWindowClientAreaCoordinatesFeature(
	private val window: MemorySegment
) : GraphicsWindowClientAreaCoordinatesFeature() {
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE
	override fun get(): GraphicsWindowClientAreaCoordinatesDataIdentifier {
		val r = autoArena.allocate(RECT)
		val status = nativeGetClientRect!!.invokeExact(
			capturedStateSegment,
			window,
			r
		) as Int
		if (status == 0) throwLastError()
		return GraphicsWindowRectangleI(
			Point2(
				RECT_left.get(r, 0L) as Int,
				RECT_top.get(r, 0L) as Int,
			),
			Point2(
				RECT_right.get(r, 0L) as Int,
				RECT_bottom.get(r, 0L) as Int,
			)
		)
	}
}