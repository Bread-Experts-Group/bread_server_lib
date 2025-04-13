package bread_experts_group.dns

import bread_experts_group.readString
import bread_experts_group.warn
import bread_experts_group.writeString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

fun readLabel(stream: InputStream, lookbehind: ByteArray): String {
	var name = ""
	var byte = stream.read()
	while (byte > 0) {
		when (byte and 0b11000000) {
			0b00000000 -> name += "${stream.readString(byte)}."
			0b11000000 -> {
				name += readLabel(
					ByteArrayInputStream(lookbehind).also { it.skip(stream.read().toLong()) },
					lookbehind
				)
				break
			}

			else -> throw UnsupportedOperationException(byte.toString())
		}
		byte = stream.read()
	}
	return name
}

fun writeLabel(label: String) = ByteArrayOutputStream().use {
	warn(label)
	label.split('.').forEach { s ->
		it.write(s.length)
		it.writeString(s)
	}
	it.toByteArray()
}