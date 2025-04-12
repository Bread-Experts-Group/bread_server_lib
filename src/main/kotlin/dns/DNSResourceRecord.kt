package bread_experts_group.dns

import bread_experts_group.SmartToString
import bread_experts_group.Writable
import bread_experts_group.read16
import bread_experts_group.read32
import bread_experts_group.write16
import bread_experts_group.write32
import bread_experts_group.writeString
import java.io.InputStream
import java.io.OutputStream

class DNSResourceRecord(
	name: String,
	val rrType: DNSType,
	val rrClass: DNSClass,
	val timeToLive: Int,
	val rrData: ByteArray
) : SmartToString(), Writable {
	val name: String = if (name.endsWith('.')) name else "$name."

	override fun write(stream: OutputStream) {
		name.split('.').forEach {
			stream.write(it.length)
			stream.writeString(it)
		}
		stream.write16(rrType.code)
		stream.write16(rrClass.code)
		stream.write32(timeToLive)
		stream.write16(rrData.size)
		stream.write(rrData)
	}

	override fun gist(): String = "(DNS, Record) \"$name\" $rrType $rrClass (${timeToLive}s), # DATA: [${rrData.size}]"

	companion object {
		fun read(stream: InputStream): DNSResourceRecord {
			return DNSResourceRecord(
				readLabel(stream),
				DNSType.mapping.getValue(stream.read16()),
				DNSClass.mapping.getValue(stream.read16()),
				stream.read32(),
				stream.readNBytes(stream.read16())
			)
		}
	}
}