package org.bread_experts_group.generic.protocol.minecraft.api

import org.bread_experts_group.generic.Mappable

enum class MinecraftClientLibraryRuleOperatingSystemName(
	override val id: String
) : Mappable<MinecraftClientLibraryRuleOperatingSystemName, String> {
	WINDOWS("windows"),
	LINUX("linux"),
	MAC("osx");

	override val tag: String = name
	override fun toString(): String = stringForm()
}