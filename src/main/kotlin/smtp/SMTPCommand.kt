package org.bread_experts_group.smtp

enum class SMTPCommand {
	HELO,
	EHLO,
	MAIL_FROM,
	RCPT_TO,
	DATA,
	NOOP,
	HELP,
	VRFY,
	EXPN,
	RSET,
	QUIT,
	STARTTLS,
	AUTH,
	ATRN,
	BDAT,
	ETRN,
	UNKNOWN;

	companion object {
		val mapping: Map<String, SMTPCommand> = entries.associateBy { it.name }
	}
}