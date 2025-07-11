package org.bread_experts_group.coder.format.parse.asn1.element

class ASN1String(
	id: ASN1ElementIdentifier,
	val string: String
) : ASN1Element(id, string.toByteArray(Charsets.UTF_8)) {
	override fun toString(): String = "$tag[\"$string\"]"
}