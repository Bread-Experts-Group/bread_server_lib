package org.bread_experts_group.api.system.socket.sys_feature.windows

import java.util.concurrent.Semaphore

class WindowsSocketManager {
	private val identificationList = IdentificationList()
	private val semaphores = mutableMapOf<Int, Pair<Semaphore, WindowsEventValueContainer>>()
	fun getSemaphore(): Triple<Int, Semaphore, WindowsEventValueContainer> {
		val id = identificationList.getID()
		val (semaphore, value) = semaphores.getOrPut(id) { Semaphore(0) to WindowsEventValueContainer() }
		return Triple(id, semaphore, value)
	}

	fun releaseSemaphore(id: Int, result: Any?, exception: Throwable? = null) {
		val (semaphore, value) = semaphores.remove(id)!!
		value.value = result
		value.throwable = exception
		semaphore.release()
		identificationList.returnID(id)
	}
}