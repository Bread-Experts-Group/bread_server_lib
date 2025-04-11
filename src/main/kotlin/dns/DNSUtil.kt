package bread_experts_group.dns

import bread_experts_group.readString
import java.io.InputStream

fun readLabel(stream: InputStream): String {
	var name = ""
	var byte = stream.read()
	when (byte and 0b11000000) {
		0b00000000 -> while (true) {
			val part = stream.readString(byte)
			if (part.isEmpty()) break
			byte = stream.read()
			name += "$part."
		}

		0b11000000 -> stream.read().let { name = "TODO..." }

		else -> throw UnsupportedOperationException(byte.toString())
	}
	return name
}