package org.bread_experts_group.coder.format.asn1.element

import java.io.OutputStream

class ASN1Tagged(
	tagNumber: Int,
	private val constructed: Boolean,
	val element: ASN1Element
) : ASN1Element((if (constructed) 0xA0 else 0x80) + tagNumber, byteArrayOf()) {
	val encoded = element.asBytes()

	override fun computeSize(): Long = encoded.size.toLong() - (if (constructed) 0 else 2)
	override fun writeExtra(stream: OutputStream) {
		stream.write(if (constructed) encoded else encoded.sliceArray(2 until encoded.size))
	}
}