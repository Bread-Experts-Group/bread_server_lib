package org.bread_experts_group.generic.protocol.minecraft.api

import org.bread_experts_group.generic.io.IOLayout
import org.bread_experts_group.generic.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.generic.io.SequentialIOLayout
import java.net.URI

data class MinecraftClientDownload(
	val sha1: ByteArray,
	val size: UInt,
	val uri: URI
) {
	companion object {
		fun decode(
			sha1: String,
			size: UInt,
			uri: String
		): MinecraftClientDownload = MinecraftClientDownload(
			sha1.hexToByteArray(),
			size,
			URI(uri)
		)

		val layout = SequentialIOLayout(
			::decode,
			ISO_LATIN_1_UNBOUNDED.withName("sha1"),
			IOLayout.UNSIGNED_INT.withName("size"),
			ISO_LATIN_1_UNBOUNDED.withName("url")
		)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MinecraftClientDownload

		if (!sha1.contentEquals(other.sha1)) return false
		if (size != other.size) return false
		if (uri != other.uri) return false

		return true
	}

	override fun hashCode(): Int {
		var result = sha1.contentHashCode()
		result = 31 * result + size.hashCode()
		result = 31 * result + uri.hashCode()
		return result
	}
}