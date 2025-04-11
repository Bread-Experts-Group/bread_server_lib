package bread_experts_group.smtp

import bread_experts_group.SmartToString
import bread_experts_group.writeString
import java.io.OutputStream

class SMTPResponse(
	val code: Int,
	val message: String
) : SmartToString() {
	override fun gist(): String = "(SMTP, <Res>, $code) $message"

	fun write(stream: OutputStream) {
		val lines = message.lines()
		lines.forEachIndexed { index, line ->
			stream.writeString("$code${if (index == lines.size - 1) ' ' else '-'}$line\r\n")
		}
	}
}