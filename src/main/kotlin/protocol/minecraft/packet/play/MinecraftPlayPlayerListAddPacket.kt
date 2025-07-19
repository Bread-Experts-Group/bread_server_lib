package org.bread_experts_group.protocol.minecraft.packet.play

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.protocol.minecraft.string
import org.bread_experts_group.protocol.minecraft.varNi
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.time.DurationUnit

class MinecraftPlayPlayerListAddPacket(
	players: Map<UUID, MinecraftPlayerListAddEntry>
) : MinecraftPlayUpdatePlayerListPacket(
	MinecraftPlayerListAction.ADD,
	players.mapValues { (_, entry) ->
		val out = ByteArrayOutputStream()
		out.write(string(entry.name))
		out.write(varNi(entry.properties.size))
		entry.properties.forEach { (name, property) ->
			out.write(string(name))
			out.write(string(property.data))
			if (property.signature != null) {
				out.write(1)
				out.write(string(property.signature))
			} else out.write(0)
		}
		out.write(varNi(entry.gameMode.id))
		out.write(varNi(entry.ping.toInt(DurationUnit.MILLISECONDS)))
		out.write(0) // TODO display name
		ByteArrayChannel(out.toByteArray())
	}
)