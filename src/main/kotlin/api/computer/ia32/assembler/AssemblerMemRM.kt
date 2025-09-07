package org.bread_experts_group.api.computer.ia32.assembler

import org.bread_experts_group.api.computer.ia32.assembler.AssemblerRegister.Companion.asmRegister
import org.bread_experts_group.api.computer.ia32.instruction.RegisterType

class AssemblerMemRM(
	val address: ULong?,
	val register: AssemblerRegister?,
	val mode: BitMode
) {
	companion object {
		fun String.asmMemRM(assembler: Assembler, mode: BitMode, type: RegisterType): AssemblerMemRM? {
			if (this.startsWith('[') && this.endsWith(']')) {
				val immediate = assembler.readImmediate(this.substring(1..this.length - 2))
				return AssemblerMemRM(immediate, null, assembler.mode)
			}
			val register = this.asmRegister(assembler, mode, type) ?: return null
			return AssemblerMemRM(null, register, register.mode)
		}
	}
}