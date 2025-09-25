package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftVersionLatest<R, S>(
	val release: R,
	val snapshot: S
) {
	companion object {
		fun decode(release: String, snapshot: String): MinecraftVersionLatest<String, String> = MinecraftVersionLatest(
			release,
			snapshot
		)

		val layout = SequentialIOLayout(
			::decode,
			ISO_LATIN_1_UNBOUNDED.withName("release"),
			ISO_LATIN_1_UNBOUNDED.withName("snapshot")
		)
	}
}