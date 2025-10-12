package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

data class MinecraftClient(
	// arguments
	val assetIndex: MinecraftClientAssetIndex,
	val assets: String,
	val complianceLevel: UInt,
	val downloads: MinecraftClientDownloads,
	val id: String,
	val javaVersion: MinecraftClientJavaVersion,
	val libraries: List<MinecraftClientLibrary>,
	val logging: MinecraftClientLoggings,
	val mainClass: String,
	val minimumLauncherVersion: UInt,
	val releaseTime: TemporalAccessor,
	val time: TemporalAccessor,
	val type: MappedEnumeration<String, MinecraftVersionType>
) {
	companion object {
		fun decode(
			assetIndex: MinecraftClientAssetIndex,
			assets: String,
			complianceLevel: UInt,
			downloads: MinecraftClientDownloads,
			id: String,
			javaVersion: MinecraftClientJavaVersion,
			libraries: List<MinecraftClientLibrary>,
			logging: MinecraftClientLoggings,
			mainClass: String,
			minimumLauncherVersion: UInt,
			releaseTime: String,
			time: String,
			type: MappedEnumeration<String, MinecraftVersionType>
		): MinecraftClient = MinecraftClient(
			assetIndex,
			assets,
			complianceLevel,
			downloads,
			id,
			javaVersion,
			libraries,
			logging,
			mainClass,
			minimumLauncherVersion,
			DateTimeFormatter.ISO_INSTANT.parse(releaseTime),
			DateTimeFormatter.ISO_INSTANT.parse(time),
			type
		)

		val layout = SequentialIOLayout(
			::decode,
			MinecraftClientAssetIndex.layout.withName("assetIndex"),
			ISO_LATIN_1_UNBOUNDED.withName("assets"),
			IOLayout.UNSIGNED_INT.withName("complianceLevel"),
			MinecraftClientDownloads.layout.withName("downloads"),
			ISO_LATIN_1_UNBOUNDED.withName("id"),
			MinecraftClientJavaVersion.layout.withName("javaVersion"),
			MinecraftClientLibrary.layout.sequence(-1).withName("libraries"),
			MinecraftClientLoggings.layout.withName("logging"),
			ISO_LATIN_1_UNBOUNDED.withName("mainClass"),
			IOLayout.UNSIGNED_INT.withName("minimumLauncherVersion"),
			ISO_LATIN_1_UNBOUNDED.withName("releaseTime"),
			ISO_LATIN_1_UNBOUNDED.withName("time"),
			IOLayout.enum(
				MinecraftVersionType.entries,
				ISO_LATIN_1_UNBOUNDED.withName("type")
			),
		)
	}
}