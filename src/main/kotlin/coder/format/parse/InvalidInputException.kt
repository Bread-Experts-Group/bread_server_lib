package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.ParsingException

class InvalidInputException(reason: String, cause: Throwable? = null) : ParsingException(reason, cause)