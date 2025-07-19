package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

object EmptyChannel : SeekableByteChannel {
	override fun size(): Long = 0L
	override fun position(): Long = 0
	override fun position(newPosition: Long): SeekableByteChannel = this
	override fun read(dst: ByteBuffer?): Int = -1
	override fun write(src: ByteBuffer?): Int = 0
	override fun truncate(size: Long): SeekableByteChannel = this
	override fun isOpen(): Boolean = true
	override fun close() {}
}