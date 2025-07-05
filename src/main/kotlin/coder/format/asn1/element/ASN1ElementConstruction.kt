package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.coder.Mappable

enum class ASN1ElementConstruction(
	override var id: Int, override val tag: String
) : Mappable<ASN1ElementConstruction, Int> {
	PRIMITIVE(0b0, "Primitive"),
	CONSTRUCTED(0b1, "Constructed");

	override fun toString(): String = stringForm()
}