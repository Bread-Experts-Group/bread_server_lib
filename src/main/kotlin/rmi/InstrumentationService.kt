package bread_experts_group.rmi

import java.io.Serializable
import java.rmi.Remote
import java.rmi.RemoteException

interface InstrumentationService : Serializable, Remote {
	@Throws(RemoteException::class)
	fun threads(): List<String>
}