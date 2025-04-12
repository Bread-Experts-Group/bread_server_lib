package bread_experts_group.dns

import bread_experts_group.SmartToString
import bread_experts_group.Writable
import bread_experts_group.read16
import bread_experts_group.write16
import bread_experts_group.writeString
import java.io.InputStream
import java.io.OutputStream

class DNSQuestion(
	val name: String,
	val qType: DNSType,
	val qClass: DNSClass
) : SmartToString(), Writable {
	override fun write(stream: OutputStream) {
		name.split('.').forEach {
			stream.write(it.length)
			stream.writeString(it)
		}
		stream.write(0)
		stream.write16(qType.code)
		stream.write16(qClass.code)
	}

	override fun gist(): String = "(DNS, Question) \"$name\" $qType $qClass"

	companion object {
		fun read(stream: InputStream): DNSQuestion {
			return DNSQuestion(
				readLabel(stream),
				DNSType.mapping.getValue(stream.read16()),
				DNSClass.mapping.getValue(stream.read16())
			)
		}
	}
}