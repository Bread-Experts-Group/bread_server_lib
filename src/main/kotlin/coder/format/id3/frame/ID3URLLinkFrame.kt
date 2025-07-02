package org.bread_experts_group.coder.format.id3.frame

import java.net.URI

class ID3URLLinkFrame(
	tag: String,
	flags: Int,
	val uri: URI
) : ID3Frame<ID3GenericFlags>(tag, ID3GenericFlags.entries, flags, byteArrayOf()) {
	override fun toString(): String = super.toString() + "[$uri]"
}