package org.bread_experts_group.generic.logging

import kotlin.time.Duration

class LevelLogger<T : LogMessage>(
	var label: String,
	val parent: LevelLogger<T>? = null,
	val flushers: MutableList<(LevelLogger<T>, T) -> Unit> = mutableListOf()
) {
	private val log = mutableListOf<T>()
	var filter: (T) -> Boolean = { false }
	var flushLimit = 0
	var flushDelay: Duration = Duration.ZERO
	private var lastFlush = System.currentTimeMillis()

	fun log(message: T) {
		if (filter(message)) return
		log.add(message)
		if (flushDelay == Duration.ZERO || log.size > flushLimit) {
			this.flush()
			return
		}
		if (flushDelay == Duration.INFINITE) return
		val time = System.currentTimeMillis()
		if (time - lastFlush > flushDelay.inWholeMilliseconds) this.flush()
		lastFlush = time
	}

	fun flush() {
		this.log.removeIf {
			this.flushers.forEach { flusher -> flusher(this, it) }
			true
		}
		parent?.flush()
	}
}