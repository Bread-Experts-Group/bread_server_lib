package bread_experts_group.dns

import bread_experts_group.readString
import java.io.ByteArrayInputStream
import java.io.InputStream

fun readLabel(stream: InputStream, lookbehind: ByteArray): String {
	var name = ""
	var byte = stream.read()
	when (byte and 0b11000000) {
		0b00000000 -> while (true) {
			val part = stream.readString(byte)
			if (part.isEmpty()) break
			byte = stream.read()
			name += "$part."
		}

		0b11000000 -> name = readLabel(
			ByteArrayInputStream(lookbehind).also { it.skip(stream.read().toLong()) },
			lookbehind
		)

		else -> throw UnsupportedOperationException(byte.toString())
	}
	return name
}