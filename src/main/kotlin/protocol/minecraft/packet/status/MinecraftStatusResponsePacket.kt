package org.bread_experts_group.protocol.minecraft.packet.status

import org.bread_experts_group.channel.ByteArrayChannel
import org.bread_experts_group.coder.fixed.json.*
import org.bread_experts_group.protocol.minecraft.string
import org.bread_experts_group.protocol.minecraft.varSizeOfNi
import java.math.BigDecimal
import java.nio.channels.SeekableByteChannel
import java.util.*

class MinecraftStatusResponsePacket(
	versionName: String,
	versionProtocol: Long,
	playersMax: Int? = null,
	playersOnline: Int? = null,
	playersSample: List<Pair<String, UUID>> = listOf(),
	description: String? = null,
	favicon: String? = null,
	enforcesSecureChat: Boolean = false
) : MinecraftStatusPacket(
	MinecraftStatusPacketType.REQUEST,
	run {
		val players = JSONObject()
		if (playersMax != null)
			players.entries["max"] = JSONNumber(BigDecimal.valueOf(playersMax.toLong()))
		if (playersOnline != null)
			players.entries["online"] = JSONNumber(BigDecimal.valueOf(playersOnline.toLong()))
		if (playersSample.isNotEmpty()) players.entries["sample"] = JSONArray(
			playersSample.map {
				JSONObject(
					"name" to JSONString(it.first),
					"id" to JSONString(it.second.toString())
				)
			}.toTypedArray()
		)
		val info = JSONObject(
			"version" to JSONObject(
				"name" to JSONString(versionName),
				"protocol" to JSONNumber(BigDecimal.valueOf(versionProtocol)),
			),
			"enforcesSecureChat" to JSONBoolean(enforcesSecureChat)
		)
		if (players.entries.isNotEmpty()) info.entries["players"] = players
		if (description != null) info.entries["description"] = JSONObject(
			"text" to JSONString(description)
		)
		if (favicon != null) info.entries["favicon"] = JSONString(favicon)
		ByteArrayChannel(string(info.toString()))
	}
) {
	override fun computeSize(): Int = varSizeOfNi(type.id) + (data as SeekableByteChannel).size().toInt()
}