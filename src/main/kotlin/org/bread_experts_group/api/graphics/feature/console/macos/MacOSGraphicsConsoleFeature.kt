package org.bread_experts_group.api.graphics.feature.console.macos

import org.bread_experts_group.api.feature.FeatureExpression
import org.bread_experts_group.api.feature.ImplementationSource
import org.bread_experts_group.api.graphics.GraphicsFeatures
import org.bread_experts_group.api.graphics.feature.console.GraphicsConsoleFeature
import org.bread_experts_group.ffi.macos.MACOS_STDOUT_FILENO
import org.bread_experts_group.ffi.macos.nativeIsatty

class MacOSGraphicsConsoleFeature : GraphicsConsoleFeature() {
	override val expresses: FeatureExpression<GraphicsConsoleFeature> = GraphicsFeatures.CUI_CONSOLE
	override val source: ImplementationSource = ImplementationSource.SYSTEM_NATIVE

	/*
		Processes on macOS already get their own "console" (stdin/out/err) by default
		when run from a shell. Checking whether stdout is a TTY (i.e., attached to a terminal
		device) is the best approximation to the Windows functionality.
		GUI apps will not have stdout attached to a terminal device.
	 */
	override fun supported(): Boolean {
		return (nativeIsatty ?: return false).invokeExact(MACOS_STDOUT_FILENO) as Int != 0
	}
}