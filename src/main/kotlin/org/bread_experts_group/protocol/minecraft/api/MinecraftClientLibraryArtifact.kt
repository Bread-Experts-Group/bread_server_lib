package org.bread_experts_group.protocol.minecraft.api

import org.bread_experts_group.io.IOLayout
import org.bread_experts_group.io.SequencedIOLayout.Companion.ISO_LATIN_1_UNBOUNDED
import org.bread_experts_group.io.SequentialIOLayout
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.Path

data class MinecraftClientLibraryArtifact(
	val path: Path,
	val sha1: ByteArray,
	val size: UInt,
	val uri: URI
) {
	companion object {
		fun decode(
			path: String,
			sha1: String,
			size: UInt,
			uri: String
		): MinecraftClientLibraryArtifact = MinecraftClientLibraryArtifact(
			Path(path),
			sha1.hexToByteArray(),
			size,
			URI(uri)
		)

		val layout = SequentialIOLayout(
			::decode,
			ISO_LATIN_1_UNBOUNDED.withName("path"),
			ISO_LATIN_1_UNBOUNDED.withName("sha1"),
			IOLayout.UNSIGNED_INT.withName("size"),
			ISO_LATIN_1_UNBOUNDED.withName("url"),
		)
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as MinecraftClientLibraryArtifact

		if (path != other.path) return false
		if (!sha1.contentEquals(other.sha1)) return false
		if (size != other.size) return false
		if (uri != other.uri) return false

		return true
	}

	override fun hashCode(): Int {
		var result = path.hashCode()
		result = 31 * result + sha1.contentHashCode()
		result = 31 * result + size.hashCode()
		result = 31 * result + uri.hashCode()
		return result
	}
}