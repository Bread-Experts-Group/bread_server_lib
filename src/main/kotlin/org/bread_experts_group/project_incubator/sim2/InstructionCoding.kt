package org.bread_experts_group.project_incubator.sim2

fun rb(processor80386: Processor80386, r: Int): Register8 = when (r) {
	0 -> processor80386.AL
	1 -> processor80386.CL
	2 -> processor80386.DL
	3 -> processor80386.BL
	4 -> processor80386.AH
	5 -> processor80386.CH
	6 -> processor80386.DH
	7 -> processor80386.BH
	else -> TODO("Invalid coding")
}

fun rw(processor80386: Processor80386, r: Int): Register16 = when (r) {
	0 -> processor80386.AX
	1 -> processor80386.CX
	2 -> processor80386.DX
	3 -> processor80386.BX
	4 -> processor80386.SP
	5 -> processor80386.BP
	6 -> processor80386.SI
	7 -> processor80386.DI
	else -> TODO("Invalid coding")
}

fun sReg(processor80386: Processor80386, r: Int): Processor80386.SegmentRegister = when (r) {
	0 -> processor80386.ES
	1 -> processor80386.CS
	2 -> processor80386.SS
	3 -> processor80386.DS
	4 -> processor80386.FS
	5 -> processor80386.GS
	else -> TODO("Invalid coding")
}

fun rd(processor80386: Processor80386, r: Int): Register32 = when (r) {
	0 -> processor80386.EAX
	1 -> processor80386.ECX
	2 -> processor80386.EDX
	3 -> processor80386.EBX
	4 -> processor80386.ESP
	5 -> processor80386.EBP
	6 -> processor80386.ESI
	7 -> processor80386.EDI
	else -> TODO("Invalid coding")
}

fun rc(processor80386: Processor80386, r: Int): Register32 = when (r) {
	0 -> processor80386.CR0
	2 -> processor80386.CR2
	3 -> processor80386.CR3
	else -> TODO()
}

fun decompose233(b: Byte): Triple<Int, Int, Int> {
	val u = b.toInt() and 0xFF
	return Triple(
		(u ushr 6),
		(u ushr 3) and 0b111,
		(u and 0b111)
	)
}

fun modRm16Addressing(
	processor80386: Processor80386, mod: Int, rm: Int,
	force8: Boolean = false
): Pair<UInt?, Register?> = when (mod) {
	0b00 -> when (rm) {
		0b000 -> TODO("000")
		0b001 -> TODO("001")
		0b010 -> TODO("010")
		0b011 -> TODO("011")
		0b100 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.SI.du
		0b101 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.DI.du
		0b110 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.instructionReadU16K()
		0b111 -> TODO("111")
		else -> TODO("Invalid coding")
	} to null

	0b01 -> (when (rm) {
		0b000 -> TODO("000")
		0b001 -> TODO("001")
		0b010 -> TODO("010")
		0b011 -> TODO("011")
		0b100 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.SI.du
		0b101 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.DI.du
		0b110 -> TODO("110")
		0b111 -> TODO("111")
		else -> TODO("Invalid coding")
	} + processor80386.instructionReadS8().toUInt()) to null

	0b10 -> TODO("10")
	0b11 -> null to if (force8) rb(processor80386, rm)
	else if (processor80386.operand32) rd(processor80386, rm)
	else rw(processor80386, rm)

	else -> TODO("Invalid coding")
}

// (scale * index) + base + displacement
fun sibAddressing(
	processor80386: Processor80386,
	scale: Int, index: Int, base: Int
): UInt = (when (index) {
	0b000 -> processor80386.EAX.du
	0b001 -> TODO("001")
	0b010 -> TODO("010")
	0b011 -> TODO("011")
	0b100 -> TODO("100")
	0b101 -> TODO("101")
	0b110 -> TODO("110")
	0b111 -> TODO("111")
	else -> TODO("Invalid coding")
} * when (scale) {
	0b00 -> 1u
	0b01 -> 2u
	0b10 -> 4u
	0b11 -> 8u
	else -> TODO("Invalid coding")
}) + when (base) {
	0b000 -> TODO("000")
	0b001 -> TODO("001")
	0b010 -> TODO("010")
	0b011 -> TODO("011")
	0b100 -> TODO("100")
	0b101 -> when (scale) {
		0b00 -> TODO("00")
		0b01 -> TODO("01")
		0b10 -> (processor80386.segmentOverride ?: processor80386.SS).base +
				processor80386.EBP.qu + processor80386.instructionReadU32K()

		0b11 -> TODO("11")
		else -> TODO("Invalid coding")
	}

	0b110 -> TODO("110")
	0b111 -> TODO("111")
	else -> TODO("Invalid coding")
}

fun modRm32Addressing(
	processor80386: Processor80386, mod: Int, rm: Int,
	force8: Boolean = false
): Pair<UInt?, Register?> = when (mod) {
	0b00 -> when (rm) {
		0b000 -> TODO("000")
		0b001 -> TODO("001")
		0b010 -> TODO("010")
		0b011 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.EBX.qu
		0b100 -> {
			val (scale, index, base) = decompose233(processor80386.instructionReadS8())
			sibAddressing(processor80386, scale, index, base)
		}

		0b101 -> TODO("101")
		0b110 -> TODO("110")
		0b111 -> TODO("111")
		else -> TODO("Invalid coding")
	} to null

	0b01 -> (when (rm) {
		0b000 -> TODO("000")
		0b001 -> TODO("001")
		0b010 -> TODO("010")
		0b011 -> (processor80386.segmentOverride ?: processor80386.DS).base + processor80386.EBX.qu
		0b100 -> TODO("100")
		0b101 -> TODO("101")
		0b110 -> TODO("110")
		0b111 -> TODO("111")
		else -> TODO("Invalid coding")
	} + processor80386.instructionReadS8().toUInt()) to null

	0b10 -> TODO("10")
	0b11 -> null to if (force8) rb(processor80386, rm)
	else if (processor80386.operand32) rd(processor80386, rm)
	else rw(processor80386, rm)

	else -> TODO("Invalid coding")
}

fun Short.toBitUInt() = this.toUInt() and 0xFFFFu