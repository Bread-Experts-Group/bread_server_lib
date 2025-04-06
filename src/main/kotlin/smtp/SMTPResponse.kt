package bread_experts_group.smtp

import bread_experts_group.SmartToString
import bread_experts_group.writeString
import java.io.OutputStream

class SMTPResponse(
	val code: Int,
	val message: String
) : SmartToString() {
	override fun gist(): String = "< (SMTP) $code $message"

	fun write(stream: OutputStream) {
		stream.writeString("$code $message\r\n")
	}
}