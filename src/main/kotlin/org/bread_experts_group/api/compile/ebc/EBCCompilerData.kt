package org.bread_experts_group.api.compile.ebc

data class EBCCompilerData(
	val codeBase: ULong,
	val unInitBase: ULong,
	val initBase: ULong,
	val instructionSpaceBase: ULong,
	var allocatorNatural: UInt = 0u,
	var allocatorConstant: UInt = 0u,
	var systemTableNatural: UInt = 0u,
	var systemTableConstant: UInt = 0u,
	val allocator: EBCVariableAllocator
)