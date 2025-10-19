package org.bread_experts_group.api.compile

import org.bread_experts_group.api.compile.ebc.EBCCompilerStackType

class CompilerStackException(stack: ArrayDeque<EBCCompilerStackType>, message: String) : CompilerException(
	"Error detected in stack [${stack.joinToString(", ")}]\n\t$message"
)