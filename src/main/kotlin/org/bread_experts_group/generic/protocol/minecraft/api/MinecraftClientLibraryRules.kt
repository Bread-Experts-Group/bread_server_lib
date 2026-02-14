package org.bread_experts_group.generic.protocol.minecraft.api

import org.bread_experts_group.generic.MappedEnumeration
import org.bread_experts_group.generic.io.IOLayout
import org.bread_experts_group.generic.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.generic.io.SequentialIOLayout

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