package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.channel.ByteBufferChannel
import org.bread_experts_group.protocol.old.minecraft.varNi
import org.bread_experts_group.protocol.old.minecraft.varSizeOfNi
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel

class MinecraftPlayChunkDataPacket(
	val x: Int,
	val y: Int,
	val upload: Boolean,
	val sectionsMask: Int,
	val chunkData: ByteArray,
	val blockEntities: List<Nothing?>
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.CHUNK_DATA,
	run {
		val buffer = ByteBuffer.allocate(24 + chunkData.size)
		buffer.putInt(x)
		buffer.putInt(y)
		buffer.put(if (upload) 1 else 0)
		buffer.put(varNi(sectionsMask))
		buffer.put(varNi(chunkData.size))
		buffer.put(chunkData) // TODO : Chunk Data
		buffer.put(varNi(blockEntities.size))
		// TODO : blockEntities
		buffer.flip()
		ByteBufferChannel(buffer)
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}