package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.coder.Mappable

enum class MinecraftClientJavaVersionComponent(
	override val id: String
) : Mappable<MinecraftClientJavaVersionComponent, String> {
	LEGACY("jre-legacy"),
	ALPHA("java-runtime-alpha"),
	BETA("java-runtime-beta"),
	GAMMA("java-runtime-gamma"),
	DELTA("java-runtime-delta");

	override val tag: String = name
	override fun toString(): String = stringForm()
}