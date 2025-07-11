package org.bread_experts_group.coder.format.parse.asn1.element

import org.bread_experts_group.stream.Writable
import java.io.OutputStream

data class ASN1ElementIdentifier(
	val clazz: ASN1ElementClass,
	val construction: ASN1ElementConstruction,
	val tag: ASN1Tag
) : Writable {
	override fun toString(): String = "[$tag : $clazz / $construction]"
	override fun write(stream: OutputStream) {
		val base = (clazz.id shl 6) or (construction.id shl 5) or (tag.id.coerceAtMost(0x1F))
		if (tag.id > 0x1F) throw IllegalArgumentException("${tag.id}")
		stream.write(base)
	}
}