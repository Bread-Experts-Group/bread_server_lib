package bread_experts_group.dns

import bread_experts_group.SmartToString
import bread_experts_group.read16
import bread_experts_group.read32
import bread_experts_group.readString
import bread_experts_group.write16
import bread_experts_group.write32
import bread_experts_group.writeString
import java.io.InputStream
import java.io.OutputStream

class DNSResourceRecord(
	val name: String,
	val rrType: DNSType,
	val rrClass: DNSClass,
	val timeToLive: Int,
	val rrData: String
) : SmartToString() {
	fun write(stream: OutputStream) {
		name.split('.').forEach {
			stream.write(it.length)
			stream.writeString(it)
		}
		stream.write(0)
		stream.write16(rrType.code)
		stream.write16(rrClass.code)
		stream.write32(timeToLive)
		stream.write16(rrData.length)
		stream.writeString(rrData)
	}

	override fun gist(): String = "(DNS, Record) \"$name\" $rrType $rrClass (${timeToLive}s) \"$rrData\""

	companion object {
		fun read(stream: InputStream): DNSResourceRecord {
			return DNSResourceRecord(
				readLabel(stream),
				DNSType.mapping.getValue(stream.read16()),
				DNSClass.mapping.getValue(stream.read16()),
				stream.read32(),
				stream.readString(stream.read16())
			)
		}
	}
}