package org.bread_experts_group.api.compile.ebc

class EBCCompilerData(
	val codeBase: ULong,
	val unInitBase: ULong,
	val initBase: ULong,
	val instructionSpaceBase: ULong,
	val allocatorNatural: UInt,
	val allocatorConstant: UInt
)