package org.bread_experts_group.api.system.socket.system

import org.bread_experts_group.api.system.socket.DeferredOperation
import java.util.concurrent.TimeUnit

abstract class DeferredAccept<D>(private val monitor: SocketMonitor) : DeferredOperation<D> {
	abstract fun accept(): List<D>

	override fun block(time: Long, unit: TimeUnit): List<D> {
		if (!monitor.accept.tryAcquire(time, unit)) return emptyList()
		return accept()
	}

	override fun block(): List<D> {
		monitor.accept.acquire()
		return accept()
	}
}