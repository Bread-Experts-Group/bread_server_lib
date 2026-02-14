package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.generic.MappedEnumeration

class NMEAGSVMessage(
	talker: MappedEnumeration<String, NMEATalker>,
	format: MappedEnumeration<String, NMEASentenceFormat>,
	fields: List<String>,
	checksum: UByte,
	checksumValid: Boolean,
	val messageCount: Int,
	val messageIndex: Int,
	val satellitesInView: Int,
	val satellites: List<NMEAGSVSatelliteDetail>
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
		buildList {
			for (i in 3 until message.fields.size step 4) {
				add(
					NMEAGSVSatelliteDetail(
						message.fields[i].toInt(),
						message.fields[i + 1].toIntOrNull(),
						message.fields[i + 2].toIntOrNull(),
						message.fields[i + 3].toIntOrNull()
					)
				)
			}
		}
	)

	override fun toString(): String = super.toString() +
			"\n\t\t$messageIndex / $messageCount" +
			"\n\t\tSatellites in view: $satellitesInView" +
			"\n\t\t$satellites"
}