package org.bread_experts_group.smtp

import org.bread_experts_group.socket.scanDelimiter
import java.io.InputStream

class SMTPRequest(
	val command: SMTPCommand,
	val message: String
) {
	override fun toString(): String = "(SMTP, <Req>, $command) $message"

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
					if (check != null) check to
							(if (commandStr.length >= message.length) "" else message.substring(commandStr.length + 1))
					else SMTPCommand.UNKNOWN to message
				}
			}
			return SMTPRequest(command, restMessage)
		}
	}
}