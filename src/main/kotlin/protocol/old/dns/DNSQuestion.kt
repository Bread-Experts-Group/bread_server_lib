package org.bread_experts_group.protocol.old.dns

import org.bread_experts_group.coder.Mappable.Companion.id
import org.bread_experts_group.stream.read16u
import org.bread_experts_group.stream.read16ui
import org.bread_experts_group.stream.write16
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

class DNSQuestion(
	val name: DNSLabel,
	val qType: DNSType,
	val qClass: DNSClass
) {
	fun write(parent: DNSMessage, stream: OutputStream) {
		if (parent.truncated) return
		val data = ByteArrayOutputStream().use {
			it.write(writeLabel((name as DNSLabelLiteral).literal))
			it.write16(qType.id.toInt())
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

	override fun toString(): String = "(DNS, Question) \"$name\" $qType $qClass"

	companion object {
		fun read(stream: InputStream, lookbehind: ByteArray): DNSQuestion = DNSQuestion(
			readLabel(stream, lookbehind),
			DNSType.entries.id(stream.read16u()).enum!!,
			DNSClass.mapping[stream.read16ui()] ?: DNSClass.OTHER
		)
	}
}