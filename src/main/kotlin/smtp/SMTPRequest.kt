package bread_experts_group.smtp

import bread_experts_group.SmartToString
import bread_experts_group.scanDelimiter
import java.io.InputStream

class SMTPRequest(
	val command: SMTPCommand,
	val message: String
) : SmartToString() {
	override fun gist(): String = "> (SMTP, $command) $message"

	companion object {
		fun read(stream: InputStream): SMTPRequest {
			val message = stream.scanDelimiter("\r\n")
			val parseMessage = message.uppercase()
			val (command, restMessage) = when (val commandStr = parseMessage.substringBefore(' ')) {
				"MAIL" -> if (parseMessage.substringBefore(':') == "MAIL FROM")
					SMTPCommand.MAIL_FROM to message.substringAfter(':')
				else SMTPCommand.UNKNOWN to message

				"RCPT" -> if (parseMessage.substringBefore(':') == "RCPT TO")
					SMTPCommand.RCPT_TO to message.substringAfter(':')
				else SMTPCommand.UNKNOWN to message

				else -> {
					val check = SMTPCommand.mapping[commandStr]
					if (check != null) check to message.substring(commandStr.length + 1)
					else SMTPCommand.UNKNOWN to message
				}
			}
			return SMTPRequest(command, restMessage)
		}
	}
}