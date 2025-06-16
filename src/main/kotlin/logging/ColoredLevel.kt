package org.bread_experts_group.logging

import org.bread_experts_group.logging.ansi_colorspace.ANSIColorSpace
import java.util.logging.Level

class ColoredLevel(
	name: String,
	val color: ANSIColorSpace, value: Int = coloredLoggerSpace++,
	resourceBundleName: String? = null
) : Level(name, value, resourceBundleName) {
	companion object {
		var coloredLoggerSpace: Int = 25777
	}
}