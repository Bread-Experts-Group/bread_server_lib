package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftClientLibraryRules(
	val action: MappedEnumeration<String, MinecraftClientLibraryRuleAction>,
	val os: MinecraftClientLibraryRuleOperatingSystem
) {
	companion object {
		fun decode(
			action: MappedEnumeration<String, MinecraftClientLibraryRuleAction>,
			os: MinecraftClientLibraryRuleOperatingSystem
		): MinecraftClientLibraryRules = MinecraftClientLibraryRules(
			action,
			os
		)

		val layout = SequentialIOLayout(
			::decode,
			IOLayout.enum(
				MinecraftClientLibraryRuleAction.entries,
				ISO_LATIN_1_UNBOUNDED.withName("action")
			),
			MinecraftClientLibraryRuleOperatingSystem.layout.withName("os"),
		)
	}
}