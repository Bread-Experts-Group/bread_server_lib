package org.bread_experts_group.coder.format.asn1.element

import java.io.ByteArrayOutputStream
import java.io.OutputStream

data class ASN1Set(
	val elements: List<ASN1Element>
) : ASN1Element(49, byteArrayOf()) {
	constructor(
		vararg elements: ASN1Element
	) : this(listOf(*elements))

	override fun toString(): String = "ASN1Set[${elements.size}]$elements"

	val encoded: ByteArray = ByteArrayOutputStream(elements.size * 2).use {
		elements.forEach { element -> element.write(it) }
		it.toByteArray()
	}

	override fun computeSize(): Long = encoded.size.toLong()
	override fun writeExtra(stream: OutputStream) {
		stream.write(encoded)
	}
}