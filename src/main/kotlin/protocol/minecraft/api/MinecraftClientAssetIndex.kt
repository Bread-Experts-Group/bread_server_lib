package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout
import java.net.URI

data class MinecraftClientAssetIndex(
	val id: String,
	val sha1: ByteArray,
	val size: UInt,
	val totalSize: UInt,
	val uri: URI
) {
	companion object {
		fun decode(
			id: String,
			sha1: String,
			size: UInt,
			totalSize: UInt,
			uri: String
		): MinecraftClientAssetIndex = MinecraftClientAssetIndex(
			id,
			sha1.hexToByteArray(),
			size,
			totalSize,
			URI(uri)
		)

		val layout = SequentialIOLayout(
			::decode,
			ISO_LATIN_1_UNBOUNDED.withName("id"),
			ISO_LATIN_1_UNBOUNDED.withName("sha1"),
			IOLayout.UNSIGNED_INT.withName("size"),
			IOLayout.UNSIGNED_INT.withName("totalSize"),
			ISO_LATIN_1_UNBOUNDED.withName("url")
		)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MinecraftClientAssetIndex

		if (id != other.id) return false
		if (!sha1.contentEquals(other.sha1)) return false
		if (size != other.size) return false
		if (totalSize != other.totalSize) return false
		if (uri != other.uri) return false

		return true
	}

	override fun hashCode(): Int {
		var result = id.hashCode()
		result = 31 * result + sha1.contentHashCode()
		result = 31 * result + size.hashCode()
		result = 31 * result + totalSize.hashCode()
		result = 31 * result + uri.hashCode()
		return result
	}
}