package org.bread_experts_group.api.compile.ebc

class EBCVariableAllocator(
	var nextFreeNatural: UInt = 0u,
	var nextFreeConstant: UInt = 0u
) {
	private val variables = mutableMapOf<Int, Pair<UInt, UInt>>()

	operator fun get(slot: Int) = variables[slot] ?: throw ArrayIndexOutOfBoundsException("No slot #$slot")

	fun getOrAllocate32(slot: Int): Pair<UInt, UInt> = variables.getOrPut(slot) {
		val savedConstant = nextFreeConstant
		nextFreeConstant += 4u
		nextFreeNatural to savedConstant
	}

	fun getOrAllocate64(slot: Int): Pair<UInt, UInt> = variables.getOrPut(slot) {
		val savedConstant = nextFreeConstant
		nextFreeConstant += 8u
		nextFreeNatural to savedConstant
	}

	fun getOrAllocateNatural(slot: Int): Pair<UInt, UInt> = variables.getOrPut(slot) {
		nextFreeNatural++ to nextFreeConstant
	}

	fun bumpNatural() = nextFreeNatural++ to nextFreeConstant
}