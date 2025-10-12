package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import java.math.BigDecimal

class NMEAGSAMessage(
	talker: MappedEnumeration<String, NMEATalker>,
	format: MappedEnumeration<String, NMEASentenceFormat>,
	fields: List<String>,
	checksum: UByte,
	checksumValid: Boolean,
	val operationMode: MappedEnumeration<Char, NMEAOperationMode>,
	val navigationMode: MappedEnumeration<Int, NMEANavigationMode>,
	val satellites: List<Int>,
	val pDOP: BigDecimal,
	val hDOP: BigDecimal,
	val vDOP: BigDecimal
) : NMEAMessage(talker, format, fields, checksum, checksumValid) {
	constructor(message: NMEAMessage) : this(
		message.talker,
		message.format,
		message.fields,
		message.checksum,
		message.checksumValid,
		NMEAOperationMode.entries.id(message.fields[0][0]),
		NMEANavigationMode.entries.id(message.fields[1].toInt()),
		message.fields.subList(2, 14).filterNot { it.isEmpty() }.map { it.toInt() },
		message.fields[14].toBigDecimal(),
		message.fields[15].toBigDecimal(),
		message.fields[16].toBigDecimal()
	)

	override fun toString(): String = super.toString() +
			"\n\t\tOp / Nav: $operationMode / $navigationMode" +
			"\n\t\tSatellites: $satellites" +
			"\n\t\tPDOP, HDOP, VDOP: $pDOP, $hDOP, $vDOP"
}