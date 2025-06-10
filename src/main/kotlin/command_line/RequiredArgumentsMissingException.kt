package org.bread_experts_group.command_line

class RequiredArgumentsMissingException(flag: Flag<*>, count: Int) : ArgumentParsingError(buildString {
	appendLine("Missing flags for [${flag.flagName}]")
	appendLine("\t${flag.flagDescription.replace("\n", "\n\t\t ")}")
	append("* Required ${flag.required} time${if (flag.required > 1) 's' else ""}, got $count")
}, flag)