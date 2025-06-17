package  org.bread_experts_group.rmi

import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import java.util.logging.Logger

class InstrumentationServiceServerTest {
	val logger: Logger = ColoredHandler.newLoggerResourced("tests.rmi")

	@Test
	fun threads() {
		logger.fine("Creating server...")
		val server = InstrumentationServiceServer.attach("BSL-Test")
		logger.fine("Created server [$server], looking up")
		val lookupServer = InstrumentationServiceServer.lookup("BSL-Test")
		logger.fine("Got [$lookupServer] for lookup, checking methods")
		val threads = lookupServer.threads()
		assertNotEquals(0, threads.size) { "Threads cannot be empty under any circumstances!" }
		logger.fine {
			buildString { threads.forEach { append(" - $it") } }
		}
	}
}