package org.bread_experts_group.protocol.minecraft.packet

import org.bread_experts_group.channel.ByteWritable
import java.nio.ByteBuffer
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel

open class MinecraftPacket(val data: ReadableByteChannel) : ByteWritable {
	override fun toString(): String = "MinecraftPacket[$data]"

	context(to: WritableByteChannel, buffer: ByteBuffer)
	override fun write(): Unit = throw UnsupportedOperationException()
	override fun computeSize(): Int = throw UnsupportedOperationException()
}