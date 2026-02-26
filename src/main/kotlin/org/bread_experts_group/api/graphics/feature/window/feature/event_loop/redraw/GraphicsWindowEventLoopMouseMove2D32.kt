package org.bread_experts_group.api.graphics.feature.window.feature.event_loop.redraw

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse_move_2d.GraphicsWindowEventLoopMouseMove2DEventParameter

data class GraphicsWindowEventLoopMouseMove2D32(
	val x: Int,
	val y: Int,
) : GraphicsWindowEventLoopMouseMove2DEventParameter