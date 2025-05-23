package org.bread_experts_group.dns

import org.bread_experts_group.stream.readString
import org.bread_experts_group.stream.writeString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

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
	label.split('.').forEach { s ->
		it.write(s.length)
		it.writeString(s)
	}
	it.toByteArray()
}