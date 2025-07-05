package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.coder.Mappable

enum class ASN1ElementClass(override var id: Int, override val tag: String) : Mappable<ASN1ElementClass, Int> {
	UNIVERSAL(0b00, "Universal"),
	APPLICATION(0b01, "Application"),
	CONTEXT_SPECIFIC(0b10, "Context-Specific"),
	PRIVATE(0b11, "Private");

	override fun toString(): String = stringForm()
}