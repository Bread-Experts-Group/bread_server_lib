package org.bread_experts_group.coder.format.asn1.element

class ASN1OctetString(
	val string: ByteArray
) : ASN1Element(4, string)