package org.bread_experts_group.coder.format.parse.asn1.element

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ASN1UTCTime(
	id: ASN1ElementIdentifier,
	val time: String
) : ASN1Element(id, time.toByteArray(Charsets.US_ASCII)) {
	constructor(
		id: ASN1ElementIdentifier,
		time: ZonedDateTime
	) : this(
		id,
		DateTimeFormatter.ofPattern("yyMMddHHmmssxx")
			.format(time)
	)

	override fun toString(): String = "$tag[\"$time\"]"
}