package org.bread_experts_group.computer.ia32.assembler

enum class AssemblerRegister(val mode: Assembler.BitMode, val registerName: String) {
	AL(Assembler.BitMode.BITS_8, "a"),
	AH(Assembler.BitMode.BITS_8, "a"),
	AX(Assembler.BitMode.BITS_16, "a"),
	EAX(Assembler.BitMode.BITS_32, "a"),
	RAX(Assembler.BitMode.BITS_64, "a"),
	CL(Assembler.BitMode.BITS_8, "c"),
	CH(Assembler.BitMode.BITS_8, "c"),
	CX(Assembler.BitMode.BITS_16, "c"),
	ECX(Assembler.BitMode.BITS_32, "c"),
	RCX(Assembler.BitMode.BITS_64, "c"),
	DL(Assembler.BitMode.BITS_8, "d"),
	DH(Assembler.BitMode.BITS_8, "d"),
	DX(Assembler.BitMode.BITS_16, "d"),
	EDX(Assembler.BitMode.BITS_32, "d"),
	RDX(Assembler.BitMode.BITS_64, "d"),
	BL(Assembler.BitMode.BITS_8, "b"),
	BH(Assembler.BitMode.BITS_8, "b"),
	BX(Assembler.BitMode.BITS_16, "b"),
	EBX(Assembler.BitMode.BITS_32, "b"),
	RBX(Assembler.BitMode.BITS_64, "b"),
	SP(Assembler.BitMode.BITS_8, "sp"),
	ESP(Assembler.BitMode.BITS_32, "sp"),
	RSP(Assembler.BitMode.BITS_64, "sp"),
	BP(Assembler.BitMode.BITS_8, "bp"),
	EBP(Assembler.BitMode.BITS_32, "bp"),
	RBP(Assembler.BitMode.BITS_64, "bp"),
	SI(Assembler.BitMode.BITS_8, "si"),
	ESI(Assembler.BitMode.BITS_32, "si"),
	RSI(Assembler.BitMode.BITS_64, "si"),
	DI(Assembler.BitMode.BITS_8, "di"),
	EDI(Assembler.BitMode.BITS_32, "di"),
	RDI(Assembler.BitMode.BITS_64, "di"),
	CR0(Assembler.BitMode.BITS_32, "cr0"),
	CR2(Assembler.BitMode.BITS_32, "cr2"),
	CR3(Assembler.BitMode.BITS_32, "cr3"),
	CR4(Assembler.BitMode.BITS_32, "cr4");

	fun regBits(): UByte = when (this) {
		AL, AX, EAX, CR0 -> 0b000u
		CL, CX, ECX -> 0b001u
		DL, DX, EDX, CR2 -> 0b010u
		BL, BX, EBX, CR3 -> 0b011u
		AH, SP, ESP, CR4 -> 0b100u
		CH, BP, EBP -> 0b101u
		DH, SI, ESI -> 0b110u
		BH, DI, EDI -> 0b111u
		else -> throw UnsupportedOperationException(this.name)
	}

	companion object {
		fun String.asmRegister(assembler: Assembler): AssemblerRegister? = entries
			.firstOrNull { it.name == this.uppercase() }
			?.also {
				if (it.name.lowercase() != this) assembler.logger.warning("casing problem: \"$this\" != \"${it.name}\"")
			}
	}
}