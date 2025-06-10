package org.bread_experts_group.logging

import org.bread_experts_group.logging.ansi_colorspace.ANSIColorSpace
import java.util.logging.Level

class ColoredLevel(name: String, val color: ANSIColorSpace, value: Int = coloredLoggerSpace++) : Level(name, value) {
	companion object {
		var coloredLoggerSpace = 25777
	}
}