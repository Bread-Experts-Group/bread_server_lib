package org.bread_experts_group.api.socket

import java.nio.ByteBuffer
import java.nio.channels.ByteChannel

interface AddressableByteChannel : ByteChannel {
	fun readDatagram(dst: ByteBuffer): Pair<ByteArray, Int>
	fun writeDatagram(src: ByteBuffer): Pair<ByteArray, Int>
	override fun read(dst: ByteBuffer): Int = this.readDatagram(dst).second
	override fun write(src: ByteBuffer): Int = this.writeDatagram(src).second
}