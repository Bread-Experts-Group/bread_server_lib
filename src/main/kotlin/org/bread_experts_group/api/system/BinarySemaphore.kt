package org.bread_experts_group.api.system

import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class BinarySemaphore(status: Boolean = false) {
	private val semaphore = Semaphore(if (status) 1 else 0)
	fun acquire() = semaphore.acquire()
	fun tryAcquire(timeout: Long, unit: TimeUnit) = semaphore.tryAcquire(timeout, unit)
	fun release() = synchronized(semaphore) {
		if (semaphore.availablePermits() < 1) semaphore.release()
	}
}