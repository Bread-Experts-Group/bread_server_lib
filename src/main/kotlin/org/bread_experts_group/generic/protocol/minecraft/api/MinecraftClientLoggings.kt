package org.bread_experts_group.generic.protocol.minecraft.api

import org.bread_experts_group.generic.io.SequentialIOLayout

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