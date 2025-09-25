package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.coder.Mappable

enum class MinecraftVersionType(
	override val id: String
) : Mappable<MinecraftVersionType, String> {
	RELEASE("release"),
	SNAPSHOT("snapshot"),
	OLD_BETA("old_beta"),
	OLD_ALPHA("old_alpha");

	override val tag: String = name
	override fun toString(): String = stringForm()
}