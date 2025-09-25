package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftClientLibraryDownloads(
	val artifact: MinecraftClientLibraryArtifact
) {
	companion object {
		fun decode(
			artifact: MinecraftClientLibraryArtifact
		): MinecraftClientLibraryDownloads = MinecraftClientLibraryDownloads(
			artifact
		)

		val layout = SequentialIOLayout(
			::decode,
			MinecraftClientLibraryArtifact.layout.withName("artifact")
		)
	}
}