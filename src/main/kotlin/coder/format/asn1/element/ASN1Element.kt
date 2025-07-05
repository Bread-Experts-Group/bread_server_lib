package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.OutputStream

open class ASN1Element(
	override val tag: ASN1ElementIdentifier,
	open val data: ByteArray = byteArrayOf()
) : Writable, Tagged<ASN1ElementIdentifier> {
	constructor(
		t: Int,
		d: ByteArray
	) : this(
		ASN1ElementIdentifier(
			ASN1ElementClass.CONTEXT_SPECIFIC,
			ASN1ElementConstruction.PRIMITIVE,
			ASN1Tag.NULL
		), d
	)

	override fun toString(): String = "ASN1Element.$tag[#${data.size}]"

	override fun computeSize(): Long = data.size.toLong()
	final override fun write(stream: OutputStream) {
		tag.write(stream)
		val size = computeSize()
		if (size < 0x80) stream.write(size.toInt())
		else {
			var remainder = size
			var bytes = 0
			while (remainder > 0) {
				bytes++
				remainder = remainder shr 8
			}
			remainder = size
			stream.write(0x80 + bytes)
			while (remainder > 0) {
				stream.write((remainder and 0b11111111).toInt())
				remainder = remainder shr 8
			}
		}
		stream.write(data)
	}
}