package bread_experts_group.dns

import bread_experts_group.*
import java.io.InputStream
import java.io.OutputStream

class DNSResourceRecord(
	name: String,
	val rrType: DNSType,
	val rrClass: DNSClass,
	val rrClassRaw: Int,
	val timeToLive: Int,
	val rrData: ByteArray
) : Writable {
	val name: String = if (name.endsWith('.')) name else "$name."

	override fun write(stream: OutputStream) {
		name.split('.').forEach {
			stream.write(it.length)
			stream.writeString(it)
		}
		stream.write16(rrType.code)
		stream.write16(rrClassRaw)
		stream.write32(timeToLive)
		stream.write16(rrData.size)
		stream.write(rrData)
	}

	override fun toString(): String = buildString {
		append("(DNS, Record) \"$name\" $rrType ")
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
			val rrType = stream.read16()
			val rrClassRaw = stream.read16()
			return DNSResourceRecord(
				label,
				DNSType.mapping[rrType] ?: DNSType.OTHER,
				DNSClass.mapping[rrClassRaw] ?: DNSClass.OTHER,
				rrClassRaw,
				stream.read32(),
				stream.readNBytes(stream.read16())
			)
		}
	}
}