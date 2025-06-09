package org.bread_experts_group.coder.format.asn1.element

import java.io.OutputStream

data class ASN1Boolean(
	val value: Boolean
) : ASN1Element(1, byteArrayOf()) {
	override fun computeSize(): Long = 1L
	override fun writeExtra(stream: OutputStream) {
		stream.write(if (value) 0xFF else 0)
	}
}