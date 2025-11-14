package org.bread_experts_group.project_incubator.console

sealed class ConsoleMessage {
	data class WindowSize(val x: Int, val y: Int) : ConsoleMessage()
	data class MouseInput(
		val button: Int,
		val x: Int, val y: Int,
		val wheel: Boolean,
		val down: Boolean,
		var captured: Boolean
	) : ConsoleMessage()

	class Refresh : ConsoleMessage()
}