package org.bread_experts_group.api.system.socket.windows

import org.bread_experts_group.api.system.socket.DeferredSocketOperation
import java.util.concurrent.TimeUnit

abstract class DeferredSocketConnect<D>(private val monitor: WindowsSocketMonitor) : DeferredSocketOperation<D> {
	abstract fun connect(): List<D>

	override fun block(time: Long, unit: TimeUnit): List<D> {
		val data = connect()
		if (!monitor.read.tryAcquire(time, unit)) return emptyList()
		return data
	}

	override fun block(): List<D> {
		val data = connect()
		monitor.connect.acquire()
		return data
	}
}