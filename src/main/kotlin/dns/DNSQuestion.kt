package bread_experts_group.dns

import bread_experts_group.read16
import bread_experts_group.readString
import bread_experts_group.write16
import bread_experts_group.writeString
import java.io.InputStream
import java.io.OutputStream

class DNSQuestion(
	val name: String,
	val qType: DNSType,
	val qClass: DNSClass
) {
	fun write(stream: OutputStream) {
		name.split('.').forEach {
			stream.write(it.length)
			stream.writeString(it)
		}
		stream.write(0)
		stream.write16(qType.code)
		stream.write16(qClass.code)
	}

	companion object {
		fun read(stream: InputStream): DNSQuestion {
			var name = ""
			while (true) {
				val part = stream.readString(stream.read())
				if (part.isEmpty()) break
				name += "$part."
			}
			return DNSQuestion(
				name,
				DNSType.mapping.getValue(stream.read16()),
				DNSClass.mapping.getValue(stream.read16())
			)
		}
	}
}