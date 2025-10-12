package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.temporal.ChronoField

class NMEAGLLMessage(
	talker: MappedEnumeration<String, NMEATalker>,
	format: MappedEnumeration<String, NMEASentenceFormat>,
	fields: List<String>,
	checksum: UByte,
	checksumValid: Boolean,
	val latitude: NMEACoordinate?,
	val ns: MappedEnumeration<Char, NMEANorthSouth>?,
	val longitude: NMEACoordinate?,
	val ew: MappedEnumeration<Char, NMEAEastWest>?,
	val time: OffsetTime?,
	val status: MappedEnumeration<Char, NMEAReceiverStatus>,
	val positioning: MappedEnumeration<Char, NMEAPositioningMode>?
) : NMEAMessage(talker, format, fields, checksum, checksumValid) {
	constructor(message: NMEAMessage) : this(
		message.talker,
		message.format,
		message.fields,
		message.checksum,
		message.checksumValid,
		message.fields[0].let {
			if (it.length != 2) null
			else NMEACoordinate(
				it.substring(0..1).toInt(),
				it.substring(2..9).toBigDecimal()
			)
		},
		message.fields[1].let {
			if (it.length != 1) null
			else NMEANorthSouth.entries.id(it[0])
		},
		message.fields[2].let {
			if (it.length != 2) null
			else NMEACoordinate(
				it.substring(0..2).toInt(),
				it.substring(3..10).toBigDecimal()
			)
		},
		message.fields[3].let {
			if (it.length != 1) null
			else NMEAEastWest.entries.id(it[0])
		},
		message.fields[4].let {
			if (it.isEmpty()) return@let null
			val parsed = utcTimeFormat.parse(it)
			OffsetTime.of(
				parsed.get(ChronoField.HOUR_OF_AMPM),
				parsed.get(ChronoField.MINUTE_OF_HOUR),
				parsed.get(ChronoField.SECOND_OF_MINUTE),
				parsed.get(ChronoField.NANO_OF_SECOND),
				ZoneOffset.UTC
			)
		},
		NMEAReceiverStatus.entries.id(message.fields[5][0]),
		message.fields.getOrNull(6)?.get(0)?.let { NMEAPositioningMode.entries.id(it) },
	)

	override fun toString(): String = super.toString() +
			"\n\t\t$time" +
			"\n\t\tLatitude: $latitude ($ns)" +
			"\n\t\tLongitude: $longitude ($ew)" +
			"\n\t\t[$status / ${positioning ?: "(no positioning mode data)"}]"
}