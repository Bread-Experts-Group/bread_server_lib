package org.bread_experts_group.coder.format.asn1.element

data class ASN1Boolean(
	val value: Boolean
) : ASN1Element(1, byteArrayOf((if (value) 0xFF else 0).toByte()))