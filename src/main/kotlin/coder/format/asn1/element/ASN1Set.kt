package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.coder.format.asn1.ASN1Parser
import java.io.ByteArrayOutputStream

class ASN1Set(
	tag: ASN1ElementIdentifier,
	data: ByteArray
) : ASN1Element(tag, data), Iterable<ASN1Element> {
	constructor(
		vararg elements: ASN1Element
	) : this(
		ASN1ElementIdentifier(
			ASN1ElementClass.UNIVERSAL,
			ASN1ElementConstruction.CONSTRUCTED,
			ASN1Tag.SEQUENCE
		),
		ByteArrayOutputStream().use { dataOut ->
			elements.forEach { it.write(dataOut) }
			dataOut.toByteArray()
		}
	)

	private val parser = ASN1Parser(data.inputStream())
	override fun iterator(): Iterator<ASN1Element> = parser.iterator()

	override fun toString(): String = "$tag[#${data.size}]"
	override fun computeSize(): Long = data.size.toLong()
}