package org.bread_experts_group.coder

abstract class ParsingException(reason: String, cause: Throwable? = null) : CodingException(reason, cause)