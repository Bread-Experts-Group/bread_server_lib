package org.bread_experts_group.coder.format.parse.asn1.element

import java.math.BigInteger

data class ASN1Integer(
	val value: BigInteger
) : ASN1Element(2, value.toByteArray()) {
	constructor(value: Long) : this(BigInteger.valueOf(value))
}