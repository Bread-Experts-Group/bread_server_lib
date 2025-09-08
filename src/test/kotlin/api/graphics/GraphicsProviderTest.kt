package org.bread_experts_group.api.graphics

import org.bread_experts_group.api.graphics.feature.window.feature.GraphicsWindowFeatures
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.version
import org.junit.jupiter.api.Test

class GraphicsProviderTest {
	val logger = ColoredHandler.newLogger("tmp logger")

	@Test
	fun window() {
		val windowing = GraphicsProvider.get(GraphicsFeatures.WINDOW)
		if (windowing == null) throw UnsupportedOperationException(
			"Cannot test windowing, local system does not support ${GraphicsFeatures.WINDOW}"
		)
		val template = windowing.createTemplate()
		val window = windowing.createWindow(template)
		val windowName = window.get(GraphicsWindowFeatures.WINDOW_NAME)
		if (windowName != null) {
			logger.info(windowName.name)
			windowName.name = "kohaku no mae!"
			logger.info(windowName.name)
		}
		logger.info(window.toString())
		Thread.sleep(90000)
	}
}