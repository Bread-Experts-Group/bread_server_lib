package org.bread_experts_group.coder.format.asn1.element

class ASN1BitString(
	val string: ByteArray
) : ASN1Element(3, byteArrayOf(0, *string))