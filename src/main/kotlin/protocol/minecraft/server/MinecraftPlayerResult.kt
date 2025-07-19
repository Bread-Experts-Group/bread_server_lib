package org.bread_experts_group.protocol.minecraft.server

import org.bread_experts_group.coder.fixed.json.JSONElement.Companion.json
import java.io.InputStream

data class MinecraftPlayerResult(
	val id: String,
	val name: String
) {
	companion object {
		fun parse(input: InputStream): MinecraftPlayerResult = json(input).asObject {
			MinecraftPlayerResult(
				withString("id"),
				withString("name")
			)
		}
	}
}