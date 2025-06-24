package org.bread_experts_group.computer.arm.v4.register

class StatusRegister : Register("cpsr", 0u) {
	enum class FlagType(val position: UInt) {
		THUMB_MODE(0b0000_0000_0000_0000_0000_0000_0010_0000u),
		OVERFLOW(0b0001_0000_0000_0000_0000_0000_0000_0000u),
		CARRY(0b0010_0000_0000_0000_0000_0000_0000_0000u),
		ZERO(0b0100_0000_0000_0000_0000_0000_0000_0000u),
		NEGATIVE(0b1000_0000_0000_0000_0000_0000_0000_0000u)
	}

	fun setFlag(flag: FlagType, state: Boolean) {
		var extracted = this.value and (flag.position.inv())
		if (state) extracted = extracted or flag.position
		this.value = extracted
	}

	fun getFlag(flag: FlagType): Boolean = (this.value and flag.position) > 0u
}