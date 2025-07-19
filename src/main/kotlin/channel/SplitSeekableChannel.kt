package org.bread_experts_group.channel

import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class SplitSeekableChannel(
	channels: Collection<SeekableByteChannel>
) : SplitReadableChannel<SeekableByteChannel>(channels), SeekableByteChannel {
	override fun size(): Long = channels.sumOf { it.size() }
	override fun write(src: ByteBuffer?): Int = throw UnsupportedOperationException()
	override fun position(): Long = throw UnsupportedOperationException()
	override fun position(newPosition: Long): SeekableByteChannel = throw UnsupportedOperationException()
	override fun truncate(size: Long): SeekableByteChannel = throw UnsupportedOperationException()
}