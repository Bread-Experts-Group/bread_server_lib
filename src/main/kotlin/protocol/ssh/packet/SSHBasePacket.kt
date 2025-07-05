package org.bread_experts_group.protocol.ssh.packet

import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.write32
import java.io.OutputStream
import kotlin.random.Random

sealed class SSHBasePacket : Writable {
	override fun toString(): String = "SSHBasePacket"
	final override fun write(stream: OutputStream) {
		val payloadSize = subComputeSize()
		val blockSize = 8
		var paddingSize = Random.nextInt(4, 255)
		val excess = (payloadSize + 5 + paddingSize) % blockSize
		if (excess != 0) paddingSize += (blockSize - excess)
		stream.write32(payloadSize + 1 + paddingSize)
		stream.write(paddingSize)
		subwrite(stream)
		stream.write(Random.nextBytes(paddingSize))
	}

	abstract fun subComputeSize(): Int
	abstract fun subwrite(stream: OutputStream)
}