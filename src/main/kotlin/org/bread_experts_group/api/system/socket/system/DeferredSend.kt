package org.bread_experts_group.api.system.socket.system

import org.bread_experts_group.api.system.socket.DeferredOperation
import java.util.concurrent.TimeUnit

abstract class DeferredSend<D>(private val monitor: SocketMonitor) : DeferredOperation<D> {
	abstract fun send(): List<D>

	override fun block(time: Long, unit: TimeUnit): List<D> {
		if (!monitor.write.tryAcquire(time, unit)) return emptyList()
		return send()
	}

	override fun block(): List<D> {
		monitor.write.acquire()
		return send()
	}
}