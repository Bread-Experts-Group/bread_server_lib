package org.bread_experts_group.dns

import org.bread_experts_group.hex
import org.bread_experts_group.socket.*
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class DNSResourceRecord(
	name: String,
	val rrType: DNSType,
	val rrClass: DNSClass,
	val rrClassRaw: Int,
	val timeToLive: Long,
	val rrData: ByteArray
) {
	val name: String = if (name.endsWith('.')) name else "$name."

	fun write(parent: DNSMessage, stream: OutputStream) {
		if (parent.truncated) return
		val data = ByteArrayOutputStream().use {
			name.split('.').forEach { s ->
				it.write(s.length)
				it.writeString(s)
			}
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
			val rrType = stream.read16ui()
			val rrClassRaw = stream.read16ui()
			return DNSResourceRecord(
				label,
				DNSType.mapping[rrType] ?: DNSType.OTHER,
				DNSClass.mapping[rrClassRaw] ?: DNSClass.OTHER,
				rrClassRaw,
				stream.read32ul(),
				stream.readNBytes(stream.read16ui())
			)
		}
	}
}