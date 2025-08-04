package org.bread_experts_group.computer.ia32.assembler

import org.bread_experts_group.computer.ia32.instruction.RegisterType
import kotlin.jvm.optionals.getOrNull

enum class AssemblerRegister(val mode: BitMode, val type: RegisterType, val registerName: String) {
	AL(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "a"),
	AH(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "a"),
	AX(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "a"),
	EAX(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "a"),
	RAX(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "a"),
	CL(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "c"),
	CH(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "c"),
	CX(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "c"),
	ECX(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "c"),
	RCX(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "c"),
	DL(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "d"),
	DH(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "d"),
	DX(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "d"),
	EDX(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "d"),
	RDX(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "d"),
	BL(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "b"),
	BH(BitMode.BITS_8, RegisterType.GENERAL_PURPOSE, "b"),
	BX(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "b"),
	EBX(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "b"),
	RBX(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "b"),
	SP(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "sp"),
	ESP(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "sp"),
	RSP(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "sp"),
	BP(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "bp"),
	EBP(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "bp"),
	RBP(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "bp"),
	SI(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "si"),
	ESI(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "si"),
	RSI(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "si"),
	DI(BitMode.BITS_16, RegisterType.GENERAL_PURPOSE, "di"),
	EDI(BitMode.BITS_32, RegisterType.GENERAL_PURPOSE, "di"),
	RDI(BitMode.BITS_64, RegisterType.GENERAL_PURPOSE, "di"),
	CR0(BitMode.BITS_32, RegisterType.CONTROL, "cr0"),
	CR2(BitMode.BITS_32, RegisterType.CONTROL, "cr2"),
	CR3(BitMode.BITS_32, RegisterType.CONTROL, "cr3"),
	CR4(BitMode.BITS_32, RegisterType.CONTROL, "cr4"),
	ES(BitMode.BITS_16, RegisterType.SEGMENT, "es"),
	CS(BitMode.BITS_16, RegisterType.SEGMENT, "cs"),
	SS(BitMode.BITS_16, RegisterType.SEGMENT, "ss"),
	DS(BitMode.BITS_16, RegisterType.SEGMENT, "ds"),
	FS(BitMode.BITS_16, RegisterType.SEGMENT, "fs"),
	GS(BitMode.BITS_16, RegisterType.SEGMENT, "gs");

	fun regBits(): UByte = when (this) {
		AL, AX, EAX, CR0, ES -> 0b000u
		CL, CX, ECX, CS -> 0b001u
		DL, DX, EDX, CR2, SS -> 0b010u
		BL, BX, EBX, CR3, DS -> 0b011u
		AH, SP, ESP, CR4, FS -> 0b100u
		CH, BP, EBP, GS -> 0b101u
		DH, SI, ESI -> 0b110u
		BH, DI, EDI -> 0b111u
		else -> throw UnsupportedOperationException(this.name)
	}

	fun withinRange(assembler: Assembler, from: ArrayDeque<String>): Boolean {
		return assembler.readImmediate(from[1], this.mode.range(from[1])) != null
	}

	companion object {
		fun String.asmRegister(
			assembler: Assembler,
			mode: BitMode,
			type: RegisterType
		): AssemblerRegister? = entries.stream()
			.filter { it.mode == mode }
			.filter { it.type == type }
			.filter { it.name == this.uppercase() }
			.findFirst()
			.getOrNull()
			?.also {
				if (it.name.lowercase() != this) assembler.logger.warning("casing problem: \"$this\" != \"${it.name}\"")
			}
	}
}