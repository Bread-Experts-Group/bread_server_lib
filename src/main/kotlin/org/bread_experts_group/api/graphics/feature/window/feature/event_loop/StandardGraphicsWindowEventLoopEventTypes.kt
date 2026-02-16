package org.bread_experts_group.api.graphics.feature.window.feature.event_loop

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.resize_2d.GraphicsWindowEventLoopResize2DEventParameter

object StandardGraphicsWindowEventLoopEventTypes {
	val EVERYTHING = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult, GraphicsWindowEventLoopEventParameter> {}

	val RESIZE_2D = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopResize2DEventParameter> {}
}