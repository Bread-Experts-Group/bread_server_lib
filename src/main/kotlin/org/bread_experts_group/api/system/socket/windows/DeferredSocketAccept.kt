package org.bread_experts_group.api.system.socket.windows

import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import java.util.concurrent.TimeUnit

abstract class DeferredSocketAccept<D>(private val monitor: WindowsSocketMonitor) : DeferredSocketOperation<D> {
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