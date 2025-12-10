package org.bread_experts_group.api.system.socket.system

import org.bread_experts_group.api.system.socket.DeferredOperation
import java.util.concurrent.TimeUnit

abstract class DeferredReceive<D>(private val monitor: SocketMonitor) : DeferredOperation<D> {
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