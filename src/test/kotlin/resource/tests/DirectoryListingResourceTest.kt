package org.bread_experts_group.resource.tests

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.util.*
import java.util.logging.Logger
import kotlin.test.assertEquals

class DirectoryListingResourceTest {
	val logger: Logger = ColoredHandler.Companion.newLogger("Directory Listing Resource Tests")
	val name: String = "org.bread_experts_group.resource.DirectoryListingResource"

	@Test
	fun getContents() {
		val bundleEnUs = ResourceBundle.getBundle(name, Locale.of("en", "US"))
		val bundleJaJp = ResourceBundle.getBundle(name, Locale.of("ja", "JP"))
		fun ResourceBundle.checkBundle(forStr: String) {
			assert(this.containsKey(forStr)) { "${this.locale} is missing translation key \"$forStr\"" }
			assertEquals(this.keySet().size, bundleEnUs.keySet().size, "${this.locale} key mismatch!")
		}
		bundleEnUs.keys.iterator().forEach {
			logger.fine("Testing \"$it\"")
			bundleJaJp.checkBundle(it)
		}
	}
}