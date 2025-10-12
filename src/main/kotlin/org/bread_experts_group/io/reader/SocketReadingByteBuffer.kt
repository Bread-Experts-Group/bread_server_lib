package org.bread_experts_group.io.reader

import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import kotlin.reflect.KMutableProperty

class SocketReadingByteBuffer(
	private val selector: Selector,
	from: SocketChannel,
	buffer: ByteBuffer,
	lengthMarker: KMutableProperty<Long>?
) : ReadingByteBuffer(from, buffer, lengthMarker) {
	init {
		from.register(selector, SelectionKey.OP_READ)
	}

	override fun refill(amount: Int) {
		lengthMarker?.setter?.call(lengthMarker.getter.call() - amount)
		val toFill = amount - present
		if (toFill < 1) return
		buffer.compact()
		while (toFill > present) {
			var read = 0
			selector.select()
			for (key in selector.selectedKeys()) {
				if (!key.isReadable) continue
				val localRead = (key.channel() as ReadableByteChannel).read(buffer)
				if (localRead == -1) throw EOFException()
				read = localRead
				break
			}
			present += read
		}
		buffer.flip()
	}
}