package org.bread_experts_group.coder.format.asn1.element

import org.bread_experts_group.stream.writeString
import java.io.OutputStream
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@ConsistentCopyVisibility
data class ASN1UTCTime private constructor(
	val time: String
) : ASN1Element(23, byteArrayOf()) {
	constructor(
		time: ZonedDateTime
	) : this(
		DateTimeFormatter.ofPattern("yyMMddHHmmssxx")
			.format(time)
	)

	override fun computeSize(): Long = time.length.toLong()
	override fun writeExtra(stream: OutputStream) {
		stream.writeString(time)
	}
}