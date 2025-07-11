package org.bread_experts_group.coder.format.parse.asn1.element

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.coder.format.parse.asn1.ASN1Parser
import java.io.ByteArrayOutputStream

open class ASN1Sequence(
	tag: ASN1ElementIdentifier,
	data: ByteArray
) : ASN1Element(tag, data), Iterable<LazyPartialResult<ASN1Element, CodingException>> {
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
	override fun iterator(): Iterator<LazyPartialResult<ASN1Element, CodingException>> = parser.iterator()
	override fun toString(): String = "$tag[#${data.size}]"
}