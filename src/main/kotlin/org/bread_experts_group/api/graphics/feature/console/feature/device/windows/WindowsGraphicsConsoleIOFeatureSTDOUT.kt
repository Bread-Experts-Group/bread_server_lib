package org.bread_experts_group.api.graphics.feature.console.feature.device.windows

import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatures

class WindowsGraphicsConsoleIOFeatureSTDOUT : WindowsGraphicsConsoleIOFeature(
	GraphicsConsoleFeatures.STANDARD_OUTPUT,
	(UInt.MAX_VALUE - 11u) + 1u,
	false
)