package org.bread_experts_group.stream

import java.io.InputStream

abstract class LongInputStream : InputStream() {
	abstract fun longAvailable(): ULong
	final override fun available(): Int = longAvailable().coerceAtMost(Int.MAX_VALUE.toULong()).toInt()
}