package org.bread_experts_group.protocol.smtp

import org.bread_experts_group.stream.Writable
import org.bread_experts_group.stream.writeString
import java.io.OutputStream

class SMTPResponse(
	val code: Int,
	val message: String
) : Writable {
	override fun toString(): String = "(SMTP, <Res>, $code) $message"
	override fun write(stream: OutputStream) {
		val lines = message.lines()
		lines.forEachIndexed { index, line ->
			stream.writeString("$code${if (index == lines.size - 1) ' ' else '-'}$line\r\n")
		}
	}
}