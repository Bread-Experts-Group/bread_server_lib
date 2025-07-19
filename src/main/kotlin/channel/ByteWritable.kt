package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.WritableByteChannel

interface ByteWritable {
	context(to: WritableByteChannel, buffer: ByteBuffer)
	fun write()

	fun computeSize(): Int
}