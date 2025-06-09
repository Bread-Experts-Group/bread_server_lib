package org.bread_experts_group.coder.format.asn1.element

import java.io.OutputStream

data class ASN1OctetString(
	val string: ByteArray
) : ASN1Element(4, byteArrayOf()) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ASN1OctetString

		return string.contentEquals(other.string)
	}

	override fun hashCode(): Int {
		return string.contentHashCode()
	}

	override fun computeSize(): Long = string.size.toLong()
	override fun writeExtra(stream: OutputStream) {
		stream.write(string)
	}
}