package org.bread_experts_group.protocol.dns

import org.bread_experts_group.stream.readString
import org.bread_experts_group.stream.writeString
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

interface DNSLabel
class DNSExtendedLabel(val type: Int) : DNSLabel {
	override fun toString(): String = "[extended label: $type]"
}
class DNSLabelLiteral(literal: String) : DNSLabel {
	val literal: String = if (literal.endsWith('.')) literal else "$literal."
	override fun toString(): String = "[literal label: \"$literal\"]"
}

fun readLabel(stream: InputStream, lookbehind: ByteArray): DNSLabel {
	var name = ""
	var byte = stream.read()
	while (byte > 0) {
		when ((byte and 0b11000000) shr 6) {
			0b00 -> name += "${stream.readString(byte)}."
			0b11 -> {
				name += ((readLabel(
					ByteArrayInputStream(lookbehind).also {
						it.skip((((byte and 0b00111111) shl 8) or stream.read()).toLong())
					},
					lookbehind
				)) as DNSLabelLiteral).literal
				break
			}

			0b01 -> return DNSExtendedLabel(byte and 0b00111111)
			0b10 -> throw UnsupportedOperationException("Unallocated label type used!")
		}
		byte = stream.read()
	}
	return DNSLabelLiteral(name)
}

fun writeLabel(label: String): ByteArray = ByteArrayOutputStream().use {
	if (label == ".") it.write(0)
	else label.split('.').forEach { s ->
		it.write(s.length)
		it.writeString(s)
	}
	it.toByteArray()
}