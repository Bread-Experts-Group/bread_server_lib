package org.bread_experts_group.rmi

import org.bread_experts_group.logging.ColoredHandler
import java.lang.management.ManagementFactory
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.ExportException
import java.rmi.server.UnicastRemoteObject
import java.util.logging.Level

class InstrumentationServiceServer : UnicastRemoteObject(), InstrumentationService {
	private val threadMXBean = ManagementFactory.getThreadMXBean()

	override fun threads(): List<String> = threadMXBean.getThreadInfo(threadMXBean.allThreadIds).map { it.threadName }

	companion object {
		private val logger = ColoredHandler.newLoggerResourced("rmi")
		val registry: Registry = try {
			logger.fine("Creating RMI registry [25799]")
			LocateRegistry.createRegistry(25799)
		} catch (e: ExportException) {
			logger.log(Level.FINE, e) { "Failed to create registry, dialing RMI registry" }
			LocateRegistry.getRegistry(25799)
		}

		fun attach(name: String): InstrumentationService {
			val server = InstrumentationServiceServer()
			logger.fine { "Attaching service [$server] to [$name]" }
			registry.bind("InstrumentationService-$name", server)
			logger.fine { "Attached service [$server] to [$name]" }
			return server as InstrumentationService
		}

		fun lookup(name: String): InstrumentationService {
			logger.fine { "Looking up service [$name]" }
			val lookup = registry.lookup("InstrumentationService-$name") as InstrumentationService
			logger.fine { "Got service [$lookup] for [$name]" }
			return lookup
		}
	}
}