package org.bread_experts_group.protocol.old.minecraft.packet.play

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.protocol.old.minecraft.string
import org.bread_experts_group.protocol.old.minecraft.varNi
import org.bread_experts_group.protocol.old.minecraft.varSizeOfNi
import java.io.ByteArrayOutputStream
import java.nio.channels.SeekableByteChannel

class MinecraftPlayStatisticsPacket(
	val statistics: Map<String, Int>
) : MinecraftPlayPacket(
	MinecraftPlayPacketType.STATISTICS,
	run {
		val output = ByteArrayOutputStream()
		output.write(varNi(statistics.size))
		statistics.forEach {
			output.write(string(it.key))
			output.write(varNi(it.value))
		}
		ByteArrayChannel(output.toByteArray())
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id.id) + (data as SeekableByteChannel).size().toInt()
}