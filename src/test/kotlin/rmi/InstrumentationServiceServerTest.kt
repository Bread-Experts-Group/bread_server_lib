package rmi

import org.bread_experts_group.logging.ColoredLogger
import org.bread_experts_group.rmi.InstrumentationServiceServer
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class InstrumentationServiceServerTest {
	val logger = ColoredLogger.newLogger("Remote Method Invocation Tests")

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