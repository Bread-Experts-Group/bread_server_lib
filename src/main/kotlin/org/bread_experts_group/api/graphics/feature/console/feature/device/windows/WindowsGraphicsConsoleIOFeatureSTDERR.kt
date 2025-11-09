package org.bread_experts_group.api.graphics.feature.console.feature.device.windows

import org.bread_experts_group.api.graphics.feature.console.feature.device.GraphicsConsoleFeatures

class WindowsGraphicsConsoleIOFeatureSTDERR : WindowsGraphicsConsoleIOFeature(
	GraphicsConsoleFeatures.STANDARD_ERROR,
	(UInt.MAX_VALUE - 12u) + 1u,
	false
)