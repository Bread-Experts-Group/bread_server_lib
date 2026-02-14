package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.generic.Mappable.Companion.id
import org.bread_experts_group.generic.MappedEnumeration
import java.math.BigDecimal

class NMEAVTGMessage(
	talker: MappedEnumeration<String, NMEATalker>,
	format: MappedEnumeration<String, NMEASentenceFormat>,
	fields: List<String>,
	checksum: UByte,
	checksumValid: Boolean,
	val trueCourse: BigDecimal?,
	val magneticCourse: BigDecimal?,
	val speed: BigDecimal?,
	val positioning: MappedEnumeration<Char, NMEAPositioningMode>
) : NMEAMessage(talker, format, fields, checksum, checksumValid) {
	constructor(message: NMEAMessage) : this(
		message.talker,
		message.format,
		message.fields,
		message.checksum,
		message.checksumValid,
		message.fields[0].toBigDecimalOrNull(),
		message.fields[2].toBigDecimalOrNull(),
		message.fields[4].toBigDecimalOrNull(),
		NMEAPositioningMode.entries.id(message.fields[8][0])
	)

	override fun toString(): String = super.toString() +
			"\n\t\tCourse: " +
			buildList {
				if (trueCourse != null) add("$trueCourse° (true)")
				if (magneticCourse != null) add("$magneticCourse° (magnetic)")
				if (isEmpty()) add("(no data)")
			}.joinToString(", ") +
			"\n\t\tSpeed: $speed kts" +
			"\n\t\t$positioning"
}