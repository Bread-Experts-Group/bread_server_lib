package org.bread_experts_group.command_line

open class ArgumentParsingError(
	message: String,
	val argument: Flag<*>? = null,
	cause: Throwable? = null
) : Error(buildString {
	if (argument != null) appendLine("A problem occurred while parsing [${argument.flagName}]")
	else appendLine("A problem occurred while parsing an unknown flag")
	append("\t$message")
}, cause)