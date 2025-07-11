package org.bread_experts_group.coder.format.parse.asn1.element

data class ASN1UTFString(
	val string: String
) : ASN1Element(12, string.toByteArray(Charsets.UTF_8))