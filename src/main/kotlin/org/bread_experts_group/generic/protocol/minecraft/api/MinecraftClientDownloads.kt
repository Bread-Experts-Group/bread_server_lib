package org.bread_experts_group.generic.protocol.minecraft.api

import org.bread_experts_group.generic.io.SequentialIOLayout

data class MinecraftClientDownloads(
	val client: MinecraftClientDownload,
	val clientMappings: MinecraftClientDownload?,
	val server: MinecraftClientDownload,
	val serverMappings: MinecraftClientDownload?,
	val windowsServer: MinecraftClientDownload?
) {
	companion object {
		fun decode(
			client: MinecraftClientDownload,
			clientMappings: MinecraftClientDownload?,
			server: MinecraftClientDownload,
			serverMappings: MinecraftClientDownload?,
			windowsServer: MinecraftClientDownload?
		): MinecraftClientDownloads = MinecraftClientDownloads(
			client,
			clientMappings,
			server,
			serverMappings,
			windowsServer
		)

		val layout = SequentialIOLayout(
			::decode,
			MinecraftClientDownload.layout.withName("client"),
			MinecraftClientDownload.layout.withName("client_mappings").nullable(),
			MinecraftClientDownload.layout.withName("server"),
			MinecraftClientDownload.layout.withName("server_mappings").nullable(),
			MinecraftClientDownload.layout.withName("windows_server").nullable()
		)
	}
}