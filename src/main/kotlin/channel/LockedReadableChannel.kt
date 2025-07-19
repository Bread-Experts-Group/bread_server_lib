package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.util.concurrent.Semaphore
import kotlin.math.min

class LockedReadableChannel(
	private val buffer: ByteBuffer,
	private val lock: Semaphore,
	private var length: Long
) : ReadableByteChannel {
	var locked = false

	init {
		if (length > 0) {
			lock.acquire()
			locked = true
		}
	}

	override fun read(dst: ByteBuffer): Int {
		if (!locked) return -1
		val saved = buffer.limit()
		val transfer = min(
			dst.remaining(),
			length.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
		)
		buffer.limit(buffer.position() + transfer)
		dst.put(buffer)
		buffer.limit(saved)
		length -= transfer
		if (length == 0L) close()
		return transfer
	}

	override fun isOpen(): Boolean = locked
	override fun close() {
		lock.release()
		locked = false
	}

	override fun toString(): String = "LockedReadableChannel[${if (locked) "Locked, #$length bytes" else "Unlocked"}]"
}