package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.coder.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftClientLibraryRuleOperatingSystem(
	val name: MappedEnumeration<String, MinecraftClientLibraryRuleOperatingSystemName>
) {
	companion object {
		fun decode(
			name: MappedEnumeration<String, MinecraftClientLibraryRuleOperatingSystemName>
		): MinecraftClientLibraryRuleOperatingSystem = MinecraftClientLibraryRuleOperatingSystem(
			name
		)

		val layout = SequentialIOLayout(
			::decode,
			IOLayout.enum(
				MinecraftClientLibraryRuleOperatingSystemName.entries,
				ISO_LATIN_1_UNBOUNDED.withName("name")
			)
		)
	}
}