package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.computer.ia32.assembler.AssemblerRegister.Companion.asmRegister
import java.util.logging.Logger

class AssemblerMemRM(
	val address: ULong?,
	val register: AssemblerRegister?
) {
	companion object {
		fun String.asmMemRM(logger: Logger): AssemblerMemRM? {
			if (this.startsWith('[')) TODO("mem")
			return AssemblerMemRM(null, this.asmRegister(logger))
		}
	}
}