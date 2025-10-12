package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable.Companion.id
import org.bread_experts_group.MappedEnumeration
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

open class NMEAMessage(
	val talker: MappedEnumeration<String, NMEATalker>,
	val format: MappedEnumeration<String, NMEASentenceFormat>,
	val fields: List<String>,
	val checksum: UByte,
	val checksumValid: Boolean
) {
	companion object {
		val utcTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("HHmmss.SS")
		val utcDateFormat: DateTimeFormatter = DateTimeFormatterBuilder()
			.appendValue(ChronoField.DAY_OF_MONTH, 2)
			.appendValue(ChronoField.MONTH_OF_YEAR, 2)
			.appendValueReduced(ChronoField.YEAR, 2, 2, 2000)
			.toFormatter()

		fun decode(n: String): NMEAMessage {
			val checksum = n.indexOf('*')
			if (!(n.startsWith('$') && n.endsWith("\r\n") && checksum != -1))
				throw IllegalArgumentException("Invalid NMEA message: \"$n\"")
			val checksumValue = n.substring(checksum + 1, checksum + 3).hexToUByte()
			val checkString = n.substring(1, checksum)
			val computed: UByte = checkString.fold(0u.toUByte()) { b, c -> b xor c.code.toUByte() }
			val fields = checkString.split(',')
			val address = fields[0]
			val message = NMEAMessage(
				NMEATalker.entries.id(address.substring(0, 2)),
				NMEASentenceFormat.entries.id(address.substring(2, 5)),
				fields.subList(1, fields.size),
				checksumValue,
				computed == checksumValue
			)
			return when (message.format.enum) {
				NMEASentenceFormat.GGA -> NMEAGGAMessage(message)
				NMEASentenceFormat.GLL -> NMEAGLLMessage(message)
				NMEASentenceFormat.GSA -> NMEAGSAMessage(message)
				NMEASentenceFormat.GSV -> NMEAGSVMessage(message)
				NMEASentenceFormat.RMC -> NMEARMCMessage(message)
				NMEASentenceFormat.TXT -> NMEATXTMessage(message)
				NMEASentenceFormat.VTG -> NMEAVTGMessage(message)
				null -> message
				else -> TODO("${message.format}")
			}
		}
	}

	private fun checkedEncode(): String = "${talker.raw}${format.raw}${fields.joinToString(",", ",")}"
	fun encode(): String {
		val checked = checkedEncode()
		val checksum = checked.fold(0u.toUByte()) { b, c -> b xor c.code.toUByte() }
		return "$$checked*${checksum.toHexString(HexFormat.UpperCase)}\r\n"
	}

	override fun toString(): String = "NMEA 0183 v2.3 Message" +
			"\n\tAddress: [$talker: $format]" +
			"\n\tFields: $fields" +
			"\n\tChecksum: 0x${checksum.toHexString(HexFormat.UpperCase)} " +
			(if (checksumValid) "[valid]" else "[invalid]")
}