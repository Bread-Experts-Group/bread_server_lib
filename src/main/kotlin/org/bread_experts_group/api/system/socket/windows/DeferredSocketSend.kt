package org.bread_experts_group.api.system.socket.windows

import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import java.util.concurrent.TimeUnit

abstract class DeferredSocketSend<D>(private val monitor: WindowsSocketMonitor) : DeferredSocketOperation<D> {
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