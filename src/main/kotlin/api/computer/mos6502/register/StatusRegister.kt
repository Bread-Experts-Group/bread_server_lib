package org.bread_experts_group.api.computer.mos6502.register

class StatusRegister : ByteRegister("ccr", 0u) {
	enum class FlagType(val position: UByte) {
		NEGATIVE(0b10000000u),
		OVERFLOW(0b01000000u),
		BREAK(0b00010000u),
		DECIMAL(0b00001000u),
		INTERRUPT(0b00000100u),
		ZERO(0b00000010u),
		CARRY(0b00000001u)
	}

	fun setFlag(flag: FlagType, state: Boolean) {
		var extracted = this.value and (flag.position.inv())
		if (state) extracted = extracted or flag.position
		this.value = extracted
	}

	fun getFlag(flag: FlagType): Boolean = (this.value and flag.position) > 0u
}