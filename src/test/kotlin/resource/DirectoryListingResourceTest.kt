package resource

import org.bread_experts_group.logging.ColoredLogger
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class DirectoryListingResourceTest {
	val logger = ColoredLogger.newLogger("Directory Listing Resource Tests")
	val name = "org.bread_experts_group.resource.DirectoryListingResource"

	@Test
	fun getContents() {
		val bundleEnUs = ResourceBundle.getBundle(name, Locale.of("en", "US"))
		val bundleJaJp = ResourceBundle.getBundle(name, Locale.of("jp", "JA"))
		fun ResourceBundle.checkBundle(forStr: String) {
			assert(this.containsKey(forStr)) { "${this.locale} is missing translation key \"$forStr\"" }
			assertEquals(this.keySet().size, bundleEnUs.keySet().size, "${this.locale} key mismatch!")
		}
		bundleEnUs.keys.iterator().forEach {
			logger.info("Testing \"$it\"")
			bundleJaJp.checkBundle(it)
		}
	}
}