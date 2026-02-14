package org.bread_experts_group.generic.protocol.minecraft.api

import org.bread_experts_group.generic.Mappable

enum class MinecraftClientLibraryRuleAction(
	override val id: String
) : Mappable<MinecraftClientLibraryRuleAction, String> {
	ALLOW("allow");

	override val tag: String = name
	override fun toString(): String = stringForm()
}