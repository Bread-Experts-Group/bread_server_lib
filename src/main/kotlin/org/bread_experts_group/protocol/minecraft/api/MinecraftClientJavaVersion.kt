package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftClientJavaVersion(
	val component: MappedEnumeration<String, MinecraftClientJavaVersionComponent>,
	val majorVersion: UInt
) {
	companion object {
		fun decode(
			component: MappedEnumeration<String, MinecraftClientJavaVersionComponent>,
			majorVersion: UInt,
		): MinecraftClientJavaVersion = MinecraftClientJavaVersion(
			component,
			majorVersion
		)

		val layout = SequentialIOLayout(
			::decode,
			IOLayout.enum(
				MinecraftClientJavaVersionComponent.entries,
				ISO_LATIN_1_UNBOUNDED.withName("component")
			),
			IOLayout.UNSIGNED_INT.withName("majorVersion")
		)
	}
}