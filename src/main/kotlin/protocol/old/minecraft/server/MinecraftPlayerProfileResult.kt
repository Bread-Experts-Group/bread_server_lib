package org.bread_experts_group.protocol.old.minecraft.server

import org.bread_experts_group.coder.fixed.json.JSONElement.Companion.json
import java.io.InputStream

data class MinecraftPlayerProfileResult(
	val id: String,
	val name: String,
	val properties: Array<MinecraftPlayerProfileProperty>
) {
	companion object {
		fun parse(input: InputStream): MinecraftPlayerProfileResult = json(input).asObject {
			MinecraftPlayerProfileResult(
				withString("id"),
				withString("name"),
				inArray("properties") {
					it.asObject {
						MinecraftPlayerProfileProperty(
							withString("name"),
							withString("value"),
							withString("signature")
						)
					}
				}
			)
		}
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MinecraftPlayerProfileResult

		if (id != other.id) return false
		if (name != other.name) return false
		if (!properties.contentEquals(other.properties)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + name.hashCode()
		result = 31 * result + properties.contentHashCode()
		return result
	}
}