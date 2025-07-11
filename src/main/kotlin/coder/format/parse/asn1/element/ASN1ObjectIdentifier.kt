package org.bread_experts_group.coder.format.parse.asn1.element

import java.io.ByteArrayOutputStream
import java.io.OutputStream

class ASN1ObjectIdentifier(
	val values: Array<Int>
) : ASN1Element(
	6,
	run {
		val body = ByteArrayOutputStream()
		val first = 40 * values[0] + values[1]
		body.write(first)
		for (i in 2 until values.size) encodeBase128(values[i], body)
		body.toByteArray()
	}
) {
	constructor(
		vararg values: Int
	) : this(values.toTypedArray())

	companion object {
		fun encodeBase128(value: Int, stream: OutputStream) {
			var v = value
			val stack = mutableListOf<Byte>()

			do {
				stack.add((v and 0x7F).toByte())
				v = v ushr 7
			} while (v > 0)

			for (i in stack.indices.reversed()) {
				val byte = stack[i]
				if (i != 0) stream.write(byte.toInt() or 0x80)
				else stream.write(byte.toInt())
			}
		}
	}
}