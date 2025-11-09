package org.bread_experts_group.api.compile.ebc

data class EBCVariableAllocator(
	var nextFreeStringPosition: ULong,
	var nextFreeNatural: UInt = 0u,
	var nextFreeConstant: UInt = 0u
) {
	private val variables = mutableMapOf<Int, Pair<UInt, UInt>>()
	private val stringMap = mutableMapOf<String, ULong>()
	var strings: ByteArray = byteArrayOf()

	fun getOrAllocateString(string: String) = stringMap.getOrPut(string) {
		val appended = (string + "\u0000").toByteArray(Charsets.UTF_16LE)
		strings += appended
		val beforeAdd = nextFreeStringPosition
		nextFreeStringPosition += appended.size.toULong()
		beforeAdd
	}

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

	fun bump32() = nextFreeNatural to run {
		val saved = nextFreeConstant
		nextFreeConstant += 4u
		saved
	}

	fun bumpNatural() = nextFreeNatural++ to nextFreeConstant
}