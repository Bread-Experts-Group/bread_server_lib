package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.CodingException

class FallbackException(message: String, cause: Throwable? = null) : CodingException(message, cause)