package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout
import java.net.URI

data class MinecraftClientLibrary(
	val downloads: MinecraftClientLibraryDownloads,
	val name: String,
	val uri: URI?,
	val rules: List<MinecraftClientLibraryRules>?
) {
	companion object {
		fun decode(
			downloads: MinecraftClientLibraryDownloads,
			name: String,
			uri: String?,
			rules: List<MinecraftClientLibraryRules>?
		): MinecraftClientLibrary = MinecraftClientLibrary(
			downloads,
			name,
			uri?.let { URI(it) },
			rules
		)

		val layout = SequentialIOLayout(
			::decode,
			MinecraftClientLibraryDownloads.layout.withName("downloads"),
			ISO_LATIN_1_UNBOUNDED.withName("name"),
			ISO_LATIN_1_UNBOUNDED.withName("url").nullable(),
			MinecraftClientLibraryRules.layout.sequence(-1).withName("rules").nullable()
		)
	}
}