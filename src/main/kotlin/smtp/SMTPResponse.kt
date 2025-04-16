package org.bread_experts_group.smtp

import org.bread_experts_group.socket.writeString
import java.io.OutputStream

class SMTPResponse(
	val code: Int,
	val message: String
) {
	override fun toString(): String = "(SMTP, <Res>, $code) $message"

	fun write(stream: OutputStream) {
		val lines = message.lines()
		lines.forEachIndexed { index, line ->
			stream.writeString("$code${if (index == lines.size - 1) ' ' else '-'}$line\r\n")
		}
	}
}