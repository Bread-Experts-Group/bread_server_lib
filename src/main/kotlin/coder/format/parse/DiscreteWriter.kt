package org.bread_experts_group.coder.format.parse

import java.io.OutputStream

abstract class DiscreteWriter {
	abstract fun writeFull(stream: OutputStream)
}