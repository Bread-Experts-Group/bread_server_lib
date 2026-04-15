package org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left

import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left.down.GraphicsWindowEventLoopMouseLeft2DDownEventParameter
import org.bread_experts_group.api.graphics.feature.window.feature.event_loop.mouse.x2d.left.up.GraphicsWindowEventLoopMouseLeft2DUpEventParameter
import org.bread_experts_group.generic.Flaggable

enum class StandardAuxiliaryInputs : GraphicsWindowEventLoopMouseLeft2DDownEventParameter,
	GraphicsWindowEventLoopMouseLeft2DUpEventParameter, Flaggable {
	LEFT_DOWN,
	RIGHT_DOWN,
	SHIFT,
	CONTROL,
	MIDDLE_DOWN,
	X_BUTTON_1,
	X_BUTTON_2;

	override val position: Long = 1L shl ordinal
}