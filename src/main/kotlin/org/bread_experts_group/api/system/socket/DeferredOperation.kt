package org.bread_experts_group.api.system.socket

import java.util.concurrent.TimeUnit

interface DeferredOperation<D> {
	fun block(time: Long, unit: TimeUnit): List<D>
	fun block(): List<D>

	class Immediate<D>(private val immediate: List<D>) : DeferredOperation<D> {
		override fun block(): List<D> = immediate
		override fun block(time: Long, unit: TimeUnit): List<D> = immediate
	}
}