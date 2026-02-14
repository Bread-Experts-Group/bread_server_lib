package org.bread_experts_group.generic.logging

import kotlin.time.DurationUnit
import kotlin.time.toDuration

class LevelLogger<T : LogMessage>(
	var label: String,
	val parent: LevelLogger<T>? = null,
	val flushers: MutableList<(LevelLogger<T>, T) -> Unit> = mutableListOf()
) {
	private val log = mutableListOf<T>()
	var filter: (T) -> Boolean = { false }
	var flushLimit = 100
	var flushDelay = 2.toDuration(DurationUnit.SECONDS)
	private var lastFlush = System.currentTimeMillis()

	fun log(message: T) {
		if (filter(message)) return
		log.add(message)
		if (log.size > flushLimit) {
			this.flush()
			return
		}
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