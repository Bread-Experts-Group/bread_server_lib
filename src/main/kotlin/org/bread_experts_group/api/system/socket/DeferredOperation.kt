package org.bread_experts_group.api.system.socket

import kotlin.time.Duration

interface DeferredOperation<D> {
	fun block(duration: Duration): List<D>
	fun block(): List<D> = block(Duration.INFINITE)

	class Immediate<D>(private val immediate: List<D>) : DeferredOperation<D> {
		override fun block(duration: Duration): List<D> = immediate
	}
}