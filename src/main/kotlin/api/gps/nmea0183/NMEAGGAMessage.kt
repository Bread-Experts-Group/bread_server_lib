package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.coder.MappedEnumeration
import java.math.BigDecimal
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.temporal.ChronoField

class NMEAGGAMessage(
	talker: MappedEnumeration<String, NMEATalker>,
	format: MappedEnumeration<String, NMEASentenceFormat>,
	fields: List<String>,
	checksum: UByte,
	checksumValid: Boolean,
	val time: OffsetTime,
	val latitude: NMEACoordinate?,
	val ns: MappedEnumeration<Char, NMEANorthSouth>?,
	val longitude: NMEACoordinate?,
	val ew: MappedEnumeration<Char, NMEAEastWest>?,
	val quality: MappedEnumeration<Int, NMEAQualityIndicator>,
	val satellitesUsed: Int,
	val hDOP: BigDecimal,
	val altitude: BigDecimal?,
	val geoidSeparation: BigDecimal?,
	val differentialAge: BigDecimal?,
	val differentialStation: Int?
) : NMEAMessage(talker, format, fields, checksum, checksumValid) {
	constructor(message: NMEAMessage) : this(
		message.talker,
		message.format,
		message.fields,
		message.checksum,
		message.checksumValid,
		message.fields[0].let {
			val parsed = utcTimeFormat.parse(it)
			OffsetTime.of(
				parsed.get(ChronoField.HOUR_OF_AMPM),
				parsed.get(ChronoField.MINUTE_OF_HOUR),
				parsed.get(ChronoField.SECOND_OF_MINUTE),
				parsed.get(ChronoField.NANO_OF_SECOND),
				ZoneOffset.UTC
			)
		},
		message.fields[1].let {
			if (it.length != 2) null
			else NMEACoordinate(
				it.substring(0..1).toInt(),
				it.substring(2..9).toBigDecimal()
			)
		},
		message.fields[2].let {
			if (it.length != 1) null
			else NMEANorthSouth.entries.id(it[0])
		},
		message.fields[3].let {
			if (it.length != 2) null
			else NMEACoordinate(
				it.substring(0..2).toInt(),
				it.substring(3..10).toBigDecimal()
			)
		},
		message.fields[4].let {
			if (it.length != 1) null
			else NMEAEastWest.entries.id(it[0])
		},
		NMEAQualityIndicator.entries.id(message.fields[5].toInt()),
		message.fields[6].toInt(),
		message.fields[7].toBigDecimal(),
		message.fields[8].toBigDecimalOrNull(),
		message.fields[10].toBigDecimalOrNull(),
		message.fields[12].toBigDecimalOrNull(),
		message.fields[13].toIntOrNull()
	)

	override fun toString(): String = super.toString() +
			"\n\t\t$time" +
			"\n\t\tLatitude: $latitude ($ns)" +
			"\n\t\tLongitude: $longitude ($ew)" +
			"\n\t\tAltitude / Geoid Separation: $altitude / $geoidSeparation m" +
			"\n\t\tQuality / Satellites Used: $quality / $satellitesUsed" +
			"\n\t\tHDOP: $hDOP" +
			if (differentialAge != null) "\n\t\tDifferential Age / Station: $differentialAge / $differentialStation"
			else ""
}