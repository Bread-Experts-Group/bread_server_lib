package org.bread_experts_group.protocol.old.minecraft.packet.status

import org.bread_experts_group.channel.ensureCapacity
import org.bread_experts_group.channel.transferTo
import org.bread_experts_group.protocol.old.minecraft.packet.MinecraftPacket
import org.bread_experts_group.protocol.old.minecraft.varNi
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

sealed class MinecraftStatusPacket(
	val type: MinecraftStatusPacketType,
	data: ReadableByteChannel
) : MinecraftPacket(data) {
	override fun toString(): String = "MinecraftStatusPacket.$type[$data]"

	context(to: WritableByteChannel, buffer: ByteBuffer)
	final override fun write() {
		val sizeBytes = varNi(computeSize())
		val typeBytes = varNi(type.id)
		ensureCapacity(sizeBytes.size + typeBytes.size)
		buffer.put(sizeBytes)
		buffer.put(typeBytes)
		buffer.flip()
		to.write(buffer)
		data.transferTo(to, buffer)
	}
}