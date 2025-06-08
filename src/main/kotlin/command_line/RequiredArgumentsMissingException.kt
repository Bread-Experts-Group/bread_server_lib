package org.bread_experts_group.command_line

class RequiredArgumentsMissingException(flag: Flag<*>, count: Int) : RuntimeException(buildString {
	append("Missing flags for [${flag.flagName}]\n")
	append("\t${flag.flagDescription.replace("\n", "\n\t\t ")}\n")
	append("* Required ${flag.required} time${if (flag.required > 1) 's' else ""}, got $count")
})