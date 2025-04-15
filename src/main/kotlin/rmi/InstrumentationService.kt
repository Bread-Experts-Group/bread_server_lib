package bread_experts_group.rmi

import java.io.Serializable
import java.rmi.Remote

interface InstrumentationService : Serializable, Remote {
	fun threads(): List<String>
}