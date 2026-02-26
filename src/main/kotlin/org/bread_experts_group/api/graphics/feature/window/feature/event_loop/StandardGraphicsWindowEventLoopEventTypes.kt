package org.bread_experts_group.api.graphics.feature.window.feature.event_loop

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse_move_2d.GraphicsWindowEventLoopMouseMove2DEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.redraw.GraphicsWindowEventLoopRedrawEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.resize_2d.GraphicsWindowEventLoopResize2DEventParameter

object StandardGraphicsWindowEventLoopEventTypes {
	val EVERYTHING = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult, GraphicsWindowEventLoopEventParameter> {}
	val RESIZE_2D = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopResize2DEventParameter> {}
	val MOUSE_MOVE_2D = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopMouseMove2DEventParameter> {}
	val REDRAW = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopRedrawEventParameter> {}
}