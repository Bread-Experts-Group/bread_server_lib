package org.bread_experts_group.api.system.socket.windows

import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import java.util.concurrent.TimeUnit

abstract class DeferredSocketReceive<D>(private val monitor: WindowsSocketMonitor) : DeferredSocketOperation<D> {
	abstract fun receive(): List<D>

	override fun block(time: Long, unit: TimeUnit): List<D> {
		if (!monitor.read.tryAcquire(time, unit)) return emptyList()
		return receive()
	}

	override fun block(): List<D> {
		monitor.read.acquire()
		return receive()
	}
}