package bread_experts_group.dns

import bread_experts_group.readString
import java.io.ByteArrayInputStream
import java.io.InputStream

fun readLabel(stream: InputStream, lookbehind: ByteArray): String {
	var name = ""
	var byte = stream.read()
	while (byte > 0) {
		name += when (byte and 0b11000000) {
			0b00000000 -> "${stream.readString(byte)}."
			0b11000000 -> readLabel(
				ByteArrayInputStream(lookbehind).also { it.skip(stream.read().toLong()) },
				lookbehind
			)

			else -> throw UnsupportedOperationException(byte.toString())
		}
		byte = stream.read()
	}
	return name
}