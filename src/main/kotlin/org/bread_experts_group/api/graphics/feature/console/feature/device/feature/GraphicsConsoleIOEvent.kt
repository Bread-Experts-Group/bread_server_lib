package org.bread_experts_group.api.graphics.feature.console.feature.device.feature

import java.util.*

sealed class GraphicsConsoleIOEvent {
	enum class KeyModifiers {
		CAPS_LOCK,
		NUM_LOCK,
		SCROLL_LOCK,
		LEFT_ALT,
		LEFT_CTRL,
		RIGHT_ALT,
		RIGHT_CTRL,
		SHIFT
	}

	data class Key(
		val down: Boolean,
		val count: Int,
		val keyCode: Int,
		val scanCode: Int,
		val char: Char,
		val modifiers: EnumSet<KeyModifiers>
	) : GraphicsConsoleIOEvent()

	data class WindowSize(
		val x: Int,
		val y: Int
	) : GraphicsConsoleIOEvent()

	class Pointer() : GraphicsConsoleIOEvent()
	class OperatingSystemDependent : GraphicsConsoleIOEvent()
}