package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftClientLoggings(
	val client: MinecraftClientLogging
) {
	companion object {
		fun decode(
			client: MinecraftClientLogging
		): MinecraftClientLoggings = MinecraftClientLoggings(client)

		val layout = SequentialIOLayout(
			::decode,
			MinecraftClientLogging.layout.withName("client")
		)
	}
}