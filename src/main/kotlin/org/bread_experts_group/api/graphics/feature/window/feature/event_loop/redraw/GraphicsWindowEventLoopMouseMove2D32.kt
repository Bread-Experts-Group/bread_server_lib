package org.bread_experts_group.api.graphics.feature.window.feature.event_loop.redraw

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left.down.GraphicsWindowEventLoopMouseLeft2DDownEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left.up.GraphicsWindowEventLoopMouseLeft2DUpEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.move.GraphicsWindowEventLoopMouseMove2DEventParameter

data class GraphicsWindowEventLoopMouseMove2D32(
	val x: Int,
	val y: Int,
) : GraphicsWindowEventLoopMouseMove2DEventParameter,
	GraphicsWindowEventLoopMouseLeft2DDownEventParameter,
	GraphicsWindowEventLoopMouseLeft2DUpEventParameter