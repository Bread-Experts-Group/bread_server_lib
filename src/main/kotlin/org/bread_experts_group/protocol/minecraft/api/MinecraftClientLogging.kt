package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftClientLogging(
	val argument: String,
	val file: MinecraftClientLoggingFile,
	val type: String
) {
	companion object {
		fun decode(
			argument: String,
			file: MinecraftClientLoggingFile,
			type: String
		): MinecraftClientLogging = MinecraftClientLogging(
			argument,
			file,
			type
		)

		val layout = SequentialIOLayout(
			::decode,
			ISO_LATIN_1_UNBOUNDED.withName("argument"),
			MinecraftClientLoggingFile.layout.withName("file"),
			ISO_LATIN_1_UNBOUNDED.withName("type")
		)
	}
}