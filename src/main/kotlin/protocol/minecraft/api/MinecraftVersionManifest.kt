package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.SequentialIOLayout

data class MinecraftVersionManifest(
	val latest: MinecraftVersionLatest<MinecraftVersion, MinecraftVersion>,
	val versions: Collection<MinecraftVersion>
) {
	companion object {
		fun decode(
			latest: MinecraftVersionLatest<String, String>,
			versions: List<MinecraftVersion>
		): MinecraftVersionManifest = MinecraftVersionManifest(
			MinecraftVersionLatest(
				versions.first { it.id == latest.release },
				versions.first { it.id == latest.snapshot }
			),
			versions
		)

		val layout = SequentialIOLayout(
			::decode,
			MinecraftVersionLatest.layout.withName("latest"),
			MinecraftVersion.layout.sequence(-1).withName("versions")
		)
	}
}