package org.bread_experts_group.stream

import org.bread_experts_group.numeric.coercedInt
import java.io.InputStream

abstract class LongInputStream : InputStream() {
	abstract fun longAvailable(): ULong
	final override fun available(): Int = longAvailable().coercedInt
}