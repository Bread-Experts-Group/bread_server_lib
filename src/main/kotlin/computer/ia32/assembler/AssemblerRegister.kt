package org.bread_experts_group.computer.ia32.assembler

import java.util.logging.Logger

enum class AssemblerRegister {
	AL, AH, AX, EAX, RAX,
	CL, CH, CX, ECX, RCX,
	DL, DH, DX, EDX, RDX,
	BL, BH, BX, EBX, RBX,
	SP, ESP, RSP,
	BP, EBP, RBP,
	SI, ESI, RSI,
	DI, EDI, RDI,
	CR0, CR2, CR3, CR4;

	fun regBits(): UByte = when (this) {
		AL, AX -> 0b000u
		CL, CX -> 0b001u
		DL, DX -> 0b010u
		BL, BX -> 0b011u
		AH, SP -> 0b100u
		CH, BP -> 0b101u
		DH, SI -> 0b110u
		BH, DI -> 0b111u
		else -> throw UnsupportedOperationException(this.name)
	}

	companion object {
		fun String.asmRegister(logger: Logger): AssemblerRegister? = entries
			.firstOrNull { it.name == this.uppercase() }
			?.also {
				if (it.name.lowercase() != this) logger.warning("casing problem: \"$this\" != \"${it.name}\"")
			}
	}
}