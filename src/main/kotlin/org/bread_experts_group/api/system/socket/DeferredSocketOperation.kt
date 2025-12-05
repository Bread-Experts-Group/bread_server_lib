package org.bread_experts_group.api.system.socket

import java.util.concurrent.TimeUnit

interface DeferredSocketOperation<D> {
	fun block(time: Long, unit: TimeUnit): List<D>
	fun block(): List<D>
}