package org.bread_experts_group.coder.format

import java.io.OutputStream

abstract class DiscreteWriter {
	abstract fun writeFull(stream: OutputStream)
}