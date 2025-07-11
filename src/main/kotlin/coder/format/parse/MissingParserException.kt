package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.ParsingException

class MissingParserException(
	of: Any,
	parameters: Array<out Any>
) : ParsingException("No parser for [$of] / ${parameters.toList()}")