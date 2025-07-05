package org.bread_experts_group.protocol.http.html

import org.bread_experts_group.formatMetric
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.testBase
import org.junit.jupiter.api.Test
import kotlin.io.path.Path
import kotlin.io.path.writeText
import kotlin.time.measureTimedValue

class DirectoryListingTest {
	val logger = ColoredHandler.newLogger("sl")

	@Test
	fun computeDirectoryListingHTML() {
		val nestedBase = Path(".").toRealPath()
		val (nestHTML, nestTime) = measureTimedValue {
			DirectoryListing.computeDirectoryListingHTML(nestedBase, nestedBase)
		}
		logger.info("Large nested computation took $nestTime [${nestHTML.length.toDouble().formatMetric()}B]")
		testBase.resolve("nest.html").writeText(nestHTML)
		val largeBase = Path("../../../Pictures/Screenshots").toRealPath()
		val (largeHTML, largeTime) = measureTimedValue {
			DirectoryListing.computeDirectoryListingHTML(largeBase, largeBase)
		}
		logger.info("Large computation took $largeTime [${largeHTML.length.toDouble().formatMetric()}B]")
		testBase.resolve("large.html").writeText(largeHTML)
	}

}