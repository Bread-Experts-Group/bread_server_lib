package org.bread_experts_group.api.compile.ebc

data class EBCCompilerData(
	val codeBase: ULong,
	val unInitBase: ULong,
	val initBase: ULong,
	val allocator: EBCVariableAllocator
)