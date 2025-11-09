package org.bread_experts_group.api.graphics.feature.console.feature.device.windows

import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatures

class WindowsGraphicsConsoleIOFeatureSTDIN : WindowsGraphicsConsoleIOFeature(
	GraphicsConsoleFeatures.STANDARD_INPUT,
	(UInt.MAX_VALUE - 10u) + 1u,
	true
)