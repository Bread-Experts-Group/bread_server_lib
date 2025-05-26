package org.bread_experts_group.dns

import org.bread_experts_group.dns.opt.DNSOption
import org.bread_experts_group.dns.opt.DNSOptionRecord
import org.bread_experts_group.dns.opt.DNSOptionType
import org.bread_experts_group.hex
import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.read32ul
import org.bread_experts_group.stream.write16
import org.bread_experts_group.stream.write32
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

open class DNSResourceRecord(
	val name: DNSLabel,
	val rrType: DNSType,
	val rrClass: DNSClass,
	val rrClassRaw: Int,
	val timeToLive: Long,
	val rrData: ByteArray
) {
	fun write(parent: DNSMessage, stream: OutputStream) {
		if (parent.truncated) return
		val data = ByteArrayOutputStream().use {
			it.write(writeLabel((name as DNSLabelLiteral).literal))
			it.write16(rrType.code)
			it.write16(rrClassRaw)
			it.write32(timeToLive)
			it.write16(rrData.size)
			it.write(rrData)
			it.toByteArray()
		}
		if (parent.maxLength != null && parent.currentSize + data.size > parent.maxLength) {
			parent.truncated = true
			return
		}
		parent.currentSize += data.size
		stream.write(data)
	}

	override fun toString(): String = buildString {
		append("(DNS, Record) $name $rrType ")
		if (rrType == DNSType.OPT__OPTION) {
			append("${hex(rrClassRaw)} ${hex(timeToLive)}")
		} else {
			append("$rrClass${if (rrClass == DNSClass.OTHER) "/${hex(rrClassRaw)}" else ""} ")
			append("(${timeToLive}s)")
		}
		append(", # DATA: [${rrData.size}]")
	}

	companion object {
		fun read(stream: InputStream, lookbehind: ByteArray): DNSResourceRecord {
			val label = readLabel(stream, lookbehind)
			val rrType = stream.read16ui()
			val rrClassRaw = stream.read16ui()
			val rrTTL = stream.read32ul()
			val rrData = stream.readNBytes(stream.read16ui())
			return when (rrType) {
				DNSType.OPT__OPTION.code -> DNSOptionRecord(
					rrClassRaw,
					(rrTTL shr 24).toInt(),
					((rrTTL shr 16) and 0xFF).toInt(),
					(rrTTL and 0xFFFF).toInt() == 0x8000,
					ByteArrayInputStream(rrData).use {
						val read = mutableListOf<DNSOption>()
						while (it.available() > 0) {
							val optionRaw = it.read16ui()
							val optionData = it.readNBytes(it.read16ui())
							read.add(
								DNSOption(
									DNSOptionType.mapping[optionRaw] ?: DNSOptionType.OTHER,
									optionRaw,
									optionData
								)
							)
						}
						read
					}
				)

				else -> DNSResourceRecord(
					label,
					DNSType.mapping[rrType] ?: DNSType.OTHER,
					DNSClass.mapping[rrClassRaw] ?: DNSClass.OTHER,
					rrClassRaw,
					rrTTL,
					rrData
				)
			}
		}
	}
}