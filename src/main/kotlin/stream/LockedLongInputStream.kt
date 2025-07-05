package org.bread_experts_group.stream

import java.util.concurrent.Semaphore

abstract class LockedLongInputStream(val lock: Semaphore, open var length: ULong) : LongInputStream() {
	var locked = true

	init {
		lock.acquire()
	}

	override fun longAvailable(): ULong = length
	open fun unlock(): Int {
		if (locked) {
			lock.release()
			locked = false
		}
		return -1
	}

	override fun read(): Int {
		if (length == 0uL) return unlock()
		else {
			length--
			return readLocked()
		}
	}

	abstract fun readLocked(): Int
}