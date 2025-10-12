package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.MappedEnumeration
import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout
import java.net.URI
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

data class MinecraftVersion(
	val id: String,
	val type: MappedEnumeration<String, MinecraftVersionType>,
	val uri: URI,
	val time: TemporalAccessor,
	val releaseTime: TemporalAccessor,
	val sha1: ByteArray,
	val complianceLevel: UInt
) {
	companion object {
		fun decode(
			id: String,
			type: MappedEnumeration<String, MinecraftVersionType>,
			url: String,
			time: String,
			releaseTime: String,
			sha1: String,
			complianceLevel: UInt
		): MinecraftVersion = MinecraftVersion(
			id,
			type,
			URI(url),
			DateTimeFormatter.ISO_INSTANT.parse(time),
			DateTimeFormatter.ISO_INSTANT.parse(releaseTime),
			sha1.hexToByteArray(),
			complianceLevel
		)

		val layout = SequentialIOLayout(
			::decode,
			ISO_LATIN_1_UNBOUNDED.withName("id"),
			IOLayout.enum(
				MinecraftVersionType.entries,
				ISO_LATIN_1_UNBOUNDED.withName("type")
			),
			ISO_LATIN_1_UNBOUNDED.withName("url"),
			ISO_LATIN_1_UNBOUNDED.withName("time"),
			ISO_LATIN_1_UNBOUNDED.withName("releaseTime"),
			ISO_LATIN_1_UNBOUNDED.withName("sha1"),
			IOLayout.UNSIGNED_INT.withName("complianceLevel")
		)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MinecraftVersion

		if (complianceLevel != other.complianceLevel) return false
		if (id != other.id) return false
		if (type != other.type) return false
		if (uri != other.uri) return false
		if (time != other.time) return false
		if (releaseTime != other.releaseTime) return false
		if (!sha1.contentEquals(other.sha1)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = complianceLevel.toInt()
		result = 31 * result + id.hashCode()
		result = 31 * result + type.hashCode()
		result = 31 * result + uri.hashCode()
		result = 31 * result + time.hashCode()
		result = 31 * result + releaseTime.hashCode()
		result = 31 * result + sha1.contentHashCode()
		return result
	}
}