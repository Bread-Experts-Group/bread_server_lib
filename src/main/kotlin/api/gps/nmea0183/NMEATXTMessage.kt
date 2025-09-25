package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.coder.MappedEnumeration

class NMEATXTMessage(
	talker: MappedEnumeration<String, NMEATalker>,
	format: MappedEnumeration<String, NMEASentenceFormat>,
	fields: List<String>,
	checksum: UByte,
	checksumValid: Boolean,
	val transmissionCount: Int,
	val transmissionIndex: Int,
	val messageIdentifier: Int,
	val text: String
) : NMEAMessage(talker, format, fields, checksum, checksumValid) {
	constructor(message: NMEAMessage) : this(
		message.talker,
		message.format,
		message.fields,
		message.checksum,
		message.checksumValid,
		message.fields[0].toInt(),
		message.fields[1].toInt(),
		message.fields[2].toInt(),
		message.fields[3]
	)

	override fun toString(): String = super.toString() +
			"\n\t\t(#$messageIdentifier) $transmissionIndex / $transmissionCount" +
			"\n\t\t\"$text\""
}