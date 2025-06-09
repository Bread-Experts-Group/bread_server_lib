package org.bread_experts_group.coder.format.asn1.element

import java.io.OutputStream
import java.math.BigInteger

data class ASN1Integer(
	val value: BigInteger
) : ASN1Element(2, byteArrayOf()) {
	constructor(value: Long) : this(BigInteger.valueOf(value))

	private val asBytes = value.toByteArray()

	override fun computeSize(): Long = asBytes.size.toLong()
	override fun writeExtra(stream: OutputStream) {
		stream.write(asBytes)
	}
}