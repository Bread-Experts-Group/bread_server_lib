package org.bread_experts_group.smtp

import org.bread_experts_group.CharacterWritable
import java.io.Writer

class SMTPResponse(
	val code: Int,
	val message: String
) : CharacterWritable {
	override fun toString(): String = "(SMTP, <Res>, $code) $message"
	override fun write(writer: Writer) {
		val lines = message.lines()
		lines.forEachIndexed { index, line ->
			writer.write("$code${if (index == lines.size - 1) ' ' else '-'}$line\r\n")
		}
	}
}