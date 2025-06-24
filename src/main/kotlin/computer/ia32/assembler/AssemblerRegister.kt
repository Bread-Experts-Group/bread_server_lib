package org.bread_experts_group.computer.ia32.assembler

enum class AssemblerRegister(val mode: BitMode, val registerName: String) {
	AL(BitMode.BITS_8, "a"),
	AH(BitMode.BITS_8, "a"),
	AX(BitMode.BITS_16, "a"),
	EAX(BitMode.BITS_32, "a"),
	RAX(BitMode.BITS_64, "a"),
	CL(BitMode.BITS_8, "c"),
	CH(BitMode.BITS_8, "c"),
	CX(BitMode.BITS_16, "c"),
	ECX(BitMode.BITS_32, "c"),
	RCX(BitMode.BITS_64, "c"),
	DL(BitMode.BITS_8, "d"),
	DH(BitMode.BITS_8, "d"),
	DX(BitMode.BITS_16, "d"),
	EDX(BitMode.BITS_32, "d"),
	RDX(BitMode.BITS_64, "d"),
	BL(BitMode.BITS_8, "b"),
	BH(BitMode.BITS_8, "b"),
	BX(BitMode.BITS_16, "b"),
	EBX(BitMode.BITS_32, "b"),
	RBX(BitMode.BITS_64, "b"),
	SP(BitMode.BITS_8, "sp"),
	ESP(BitMode.BITS_32, "sp"),
	RSP(BitMode.BITS_64, "sp"),
	BP(BitMode.BITS_8, "bp"),
	EBP(BitMode.BITS_32, "bp"),
	RBP(BitMode.BITS_64, "bp"),
	SI(BitMode.BITS_8, "si"),
	ESI(BitMode.BITS_32, "si"),
	RSI(BitMode.BITS_64, "si"),
	DI(BitMode.BITS_8, "di"),
	EDI(BitMode.BITS_32, "di"),
	RDI(BitMode.BITS_64, "di"),
	CR0(BitMode.BITS_32, "cr0"),
	CR2(BitMode.BITS_32, "cr2"),
	CR3(BitMode.BITS_32, "cr3"),
	CR4(BitMode.BITS_32, "cr4");

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