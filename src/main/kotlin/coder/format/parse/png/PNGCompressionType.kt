package org.bread_experts_group.coder.format.parse.png

import org.bread_experts_group.coder.Mappable

enum class PNGCompressionType(
	override val id: Int, override val tag: String
) : Mappable<PNGCompressionType, Int> {
	DEFLATE(0, "DEFLATE (RFC 1951 / PKWARE)");

	override fun toString(): String = stringForm()
}