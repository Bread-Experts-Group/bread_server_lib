package org.bread_experts_group.api.graphics.feature.console.feature.device.feature.macos

enum class MacOSGraphicsConsoleInputModes {
	IGNORE_BREAK,
	MAP_BREAK_TO_INTR,
	IGNORE_PARITY_ERRS,
	MARK_PARITY_ERRS,
	ENABLE_PARITY_CHK,
	MAP_NL_TO_CR,
	IGNORE_CR,
	MAP_CR_TO_NL,
	ENABLE_OUTPUT_CTRL_FLOW,
	ENABLE_INPUT_CTRL_FLOW,
	RESTART_AFTER_STOP,
	BELL_ON_FULL_INPUT,
	CHG_UPPER_TO_LOWER
}