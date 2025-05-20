package org.bread_experts_group.dns

import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.write16
import org.bread_experts_group.stream.writeString
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class DNSQuestion(
	name: String,
	val qType: DNSType,
	val qClass: DNSClass
) {
	val name: String = if (name.endsWith('.')) name else "$name."

	fun write(parent: DNSMessage, stream: OutputStream) {
		if (parent.truncated) return
		val data = ByteArrayOutputStream().use {
			name.split('.').forEach { s ->
				it.write(s.length)
				it.writeString(s)
			}
			it.write16(qType.code)
			it.write16(qClass.code)
			it.toByteArray()
		}
		if (parent.maxLength != null && parent.currentSize + data.size > parent.maxLength) {
			parent.truncated = true
			return
		}
		parent.currentSize += data.size
		stream.write(data)
	}

	override fun toString() = "(DNS, Question) \"$name\" $qType $qClass"

	companion object {
		fun read(stream: InputStream, lookbehind: ByteArray): DNSQuestion = DNSQuestion(
			readLabel(stream, lookbehind),
			DNSType.mapping[stream.read16ui()] ?: DNSType.OTHER,
			DNSClass.mapping[stream.read16ui()] ?: DNSClass.OTHER
		)
	}
}