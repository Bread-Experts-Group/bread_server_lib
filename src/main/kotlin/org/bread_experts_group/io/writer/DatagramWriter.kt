package org.bread_experts_group.io.writer

import org.bread_experts_group.io.BaseWritingIO
import java.net.SocketAddress
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel

class DatagramWriter(
	private val to: SocketAddress,
	private val with: DatagramChannel,
	private val data: ByteBuffer
) : BaseWritingIO {
	override fun put(b: ByteArray) {
		data.put(b)
	}

	override fun u8(n: UByte) {
		data.put(n.toByte())
	}

	override fun u16(n: UShort) {
		data.putShort(n.toShort())
	}

	override fun u32(n: UInt) {
		data.putInt(n.toInt())
	}

	override fun flush() {
		data.flip()
		with.send(data, to)
		data.clear()
	}
}