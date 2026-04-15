package org.bread_experts_group.api.graphics.feature.window.feature.event_loop

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.down.GraphicsWindowEventLoopKeyboardKeyDownEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.up.GraphicsWindowEventLoopKeyboardKeyUpEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left.down.GraphicsWindowEventLoopMouseLeft2DDownEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left.up.GraphicsWindowEventLoopMouseLeft2DUpEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.move.GraphicsWindowEventLoopMouseMove2DEventParameter
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
	val MOUSE_LEFT_2D_DOWN = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopMouseLeft2DDownEventParameter> {}
	val MOUSE_LEFT_2D_UP = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopMouseLeft2DUpEventParameter> {}
	val REDRAW = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopRedrawEventParameter> {}
	val KEYBOARD_KEY_DOWN = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopKeyboardKeyDownEventParameter> {}
	val KEYBOARD_KEY_UP = object :
		GraphicsWindowEventLoopEventType<GraphicsWindowEventLoopEventResult,
				GraphicsWindowEventLoopKeyboardKeyUpEventParameter> {}
}