package bread_experts_group.rmi

import java.lang.management.ManagementFactory
import java.rmi.registry.LocateRegistry
import java.rmi.registry.Registry
import java.rmi.server.UnicastRemoteObject

class InstrumentationServiceServer : UnicastRemoteObject(), InstrumentationService {
	private val serialVersionUID: Long = -5828708644040732560L
	private val threadMXBean = ManagementFactory.getThreadMXBean()

	override fun threads(): List<String> = threadMXBean.getThreadInfo(threadMXBean.allThreadIds).map { it.threadName }

	companion object {
		fun registry(): Registry = LocateRegistry.createRegistry(25799)

		fun attach(name: String): InstrumentationServiceServer {
			val server = InstrumentationServiceServer()
			registry().bind("InstrumentationService-$name", server)
			return server
		}

		fun lookup(name: String): InstrumentationServiceServer = registry().lookup("InstrumentationService-$name")
				as InstrumentationServiceServer
	}
}