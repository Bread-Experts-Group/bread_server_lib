package org.bread_experts_group.image

import org.w3c.dom.Node
import javax.imageio.metadata.IIOMetadata
import javax.imageio.metadata.IIOMetadataNode

class AnimatedMetadata(
	private val delayMillis: Long
) : IIOMetadata(
	false,
	"org.bread_experts_group.animated_1.0.0",
	AnimatedMetadata::class.java.canonicalName,
	null, null
) {
	override fun isReadOnly(): Boolean = false
	override fun getAsTree(formatName: String): Node {
		if (formatName != this.nativeMetadataFormatName) throw IllegalArgumentException("Unknown format [$formatName]")
		return IIOMetadataNode().apply {
			appendChild(IIOMetadataNode("delayMillis").apply {
				nodeValue = delayMillis.toString()
			})
		}
	}

	override fun mergeTree(formatName: String?, root: Node?) {
		TODO("Not yet implemented")
	}

	override fun reset() {
		TODO("Not yet implemented")
	}
}