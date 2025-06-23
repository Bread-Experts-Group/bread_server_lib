package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.computer.ia32.assembler.AssemblerRegister.Companion.asmRegister

class AssemblerMemRM(
	val address: ULong?,
	val register: AssemblerRegister?,
	val mode: Assembler.BitMode
) {
	companion object {
		fun String.asmMemRM(assembler: Assembler): AssemblerMemRM? {
			if (this.startsWith('[')) TODO("mem")
			val register = this.asmRegister(assembler) ?: return null
			return AssemblerMemRM(null, register, register.mode)
		}
	}
}