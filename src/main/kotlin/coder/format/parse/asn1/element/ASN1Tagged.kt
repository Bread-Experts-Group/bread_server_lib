package org.bread_experts_group.coder.format.parse.asn1.element

class ASN1Tagged(
	tagNumber: Int,
	private val constructed: Boolean,
	val element: ASN1Element
) : ASN1Element(
	(if (constructed) 0xA0 else 0x80) + tagNumber,
	run {
		val encoded: ByteArray = element.asBytes()
		if (constructed) encoded else encoded.sliceArray(2 until encoded.size)
	}
)