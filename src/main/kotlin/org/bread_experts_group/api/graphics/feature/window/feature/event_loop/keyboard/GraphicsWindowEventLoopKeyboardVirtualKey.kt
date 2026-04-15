package org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.down.GraphicsWindowEventLoopKeyboardKeyDownEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.keyboard.up.GraphicsWindowEventLoopKeyboardKeyUpEventParameter

enum class GraphicsWindowEventLoopKeyboardVirtualKey : GraphicsWindowEventLoopKeyboardKeyUpEventParameter,
	GraphicsWindowEventLoopKeyboardKeyDownEventParameter {
	END,
	HOME,
	LEFT_ARROW,
	UP_ARROW,
	RIGHT_ARROW,
	DOWN_ARROW,
	DELETE
}