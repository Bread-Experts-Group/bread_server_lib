package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.generic.normalize
import java.nio.ByteBuffer
import java.nio.ByteOrder

// RETURN VALUES: R7 (8Bytes or less)
// PARAMETERS: STACK
// R0 STACK POINTER
// R1 / R2 / R3 CALL PRESERVED
// R4 / R5 / R6 / R7 NOT PRESERVED
@Suppress("FunctionName", "Unused")
class EBCProcedure {
	var output = byteArrayOf()

	companion object {
		fun naturalIndex16(
			negative: Boolean,
			naturalUnits: UInt,
			constantUnits: UInt
		): UShort {
			val encoded = if (negative) 1u shl 15 else 0u
			var naturalRemainder = naturalUnits
			var naturalBits = 0u
			while (naturalRemainder > 0u) {
				naturalRemainder = naturalRemainder shr 1
				naturalBits++
			}
			naturalBits = normalize(naturalBits, 2u)
			var constantRemainder = constantUnits
			var constantBits = 0
			while (constantRemainder > 0u) {
				constantRemainder = constantRemainder shr 1
				constantBits++
			}
			if (constantBits.toUInt() + naturalBits > 12u)
				throw IllegalArgumentException("Natural / constant indices not encodable")
			return (encoded or (((naturalBits + 1u) / 2u) shl 12) or
					(constantUnits shl naturalBits.toInt()) or naturalUnits).toUShort()
		}

		fun naturalIndex32(
			negative: Boolean,
			naturalUnits: UInt,
			constantUnits: UInt
		): UInt {
			val encoded = if (negative) 1u shl 31 else 0u
			var naturalRemainder = naturalUnits
			var naturalBits = 0u
			while (naturalRemainder > 0u) {
				naturalRemainder = naturalRemainder shr 1
				naturalBits++
			}
			naturalBits = normalize(naturalBits, 4u)
			var constantRemainder = constantUnits
			var constantBits = 0
			while (constantRemainder > 0u) {
				constantRemainder = constantRemainder shr 1
				constantBits++
			}
			if (constantBits.toUInt() + naturalBits > 28u)
				throw IllegalArgumentException("Natural / constant indices not encodable")
			return (encoded or ((naturalBits / 4u) shl 28) or
					(constantUnits shl naturalBits.toInt()) or naturalUnits)
		}
	}

	private val instructionBuffer: ByteBuffer = ByteBuffer.allocate(64).also {
		it.order(ByteOrder.LITTLE_ENDIAN)
	}

	private fun addInstruction() {
		instructionBuffer.flip()
		val data = ByteArray(instructionBuffer.limit())
		instructionBuffer.get(data)
		output += data
		instructionBuffer.clear()
	}

	private fun arithmeticBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	) {
		instructionBuffer.put((opcode or (if (operand2Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4)
					or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun ADD32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x0C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun ADD64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x4C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun SUB32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x0D, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun SUB64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x4D, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun MUL32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x0E, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun MUL64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x4E, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun DIV32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x10, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun DIV64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x50, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun MOD32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x12, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun MOD64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x52, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun AND32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x14, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun AND64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x54, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun SHR32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x18, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun SHR64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x58, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	private fun cmpIwBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	) {
		instructionBuffer.put(opcode.toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or
					(if (operand1Index != null) 0b10000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun CMPI32weq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x2D, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64weq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x6D, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wlte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x2E, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wlte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x6E, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wgte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x2F, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wgte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x6F, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wulte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x30, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wulte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x70, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI32wugte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x31, operand1, operand1Indirect, operand1Index, immediate)
	}

	fun CMPI64wugte(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		cmpIwBase(0x71, operand1, operand1Indirect, operand1Index, immediate)
	}

	private fun cmpBase(
		opcode: Int,
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	) {
		instructionBuffer.put((opcode or (if (operand2Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (operand2.ordinal shl 4) or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun CMP32eq(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x05, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64eq(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x45, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32lte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x06, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64lte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x46, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32gte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x07, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64gte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x47, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32ulte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x08, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64ulte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x48, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP32ugte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x09, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun CMP64ugte(
		operand1: EBCRegisters,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		cmpBase(0x49, operand1, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDB32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x1A, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDB64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x5A, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDW32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x1B, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDW64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x5B, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDD32(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x1C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun EXTNDD64(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		arithmeticBase(0x5C, operand1, operand1Indirect, operand2, operand2Indirect, operand2Index)
	}

	fun JMP32(
		conditional: Boolean,
		conditionSet: Boolean,
		relative: Boolean,
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UInt?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x01 or (if (operand1Index != null) 0b10000000 else 0)).toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (if (relative) 0b10000 else 0)
					or (if (conditionSet) 0b1000000 else 0) or (if (conditional) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putInt(operand1Index.toInt())
		addInstruction()
	}

	fun JMP8(
		conditional: Boolean,
		conditionSet: Boolean,
		wordOffset: Byte
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x02 or (if (conditional) 0b10000000 else 0) or (if (conditionSet) 0b1000000 else 0)).toByte()
		)
		instructionBuffer.put(wordOffset)
		addInstruction()
	}

	private fun movWBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	) {
		instructionBuffer.put(
			(opcode or (if (operand2Index != null) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	private fun movDBase(
		opcode: Int,
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UInt?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UInt?
	) {
		instructionBuffer.put(
			(opcode or (if (operand2Index != null) 0b1000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (operand2.ordinal shl 4) or
					(if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putInt(operand1Index.toInt())
		if (operand2Index != null) instructionBuffer.putInt(operand2Index.toInt())
		addInstruction()
	}

	fun MOVbw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		movWBase(0x1D, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVww(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		movWBase(0x1E, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVdw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		movWBase(0x1F, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVqw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UShort?
	): EBCProcedure = this.also {
		movWBase(0x20, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVbd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UInt?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UInt?
	): EBCProcedure = this.also {
		movDBase(0x21, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVwd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UInt?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UInt?
	): EBCProcedure = this.also {
		movDBase(0x22, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVdd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UInt?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UInt?
	): EBCProcedure = this.also {
		movDBase(0x23, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	fun MOVqd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UInt?,
		operand2: EBCRegisters, operand2Indirect: Boolean, operand2Index: UInt?
	): EBCProcedure = this.also {
		movDBase(0x24, operand1, operand1Indirect, operand1Index, operand2, operand2Indirect, operand2Index)
	}

	private fun MOVIBase(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediateDataLength: EBCImmediateDataLength, move: EBCMoveTypes
	) {
		instructionBuffer.put((0x37 or ((immediateDataLength.ordinal + 1) shl 6)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (move.ordinal shl 4)
					or (if (operand1Index != null) 0b1000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
	}

	fun MOVIbw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_8_BYTE
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIbd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_8_BYTE
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIbq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_8_BYTE
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVIww(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_16_WORD
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIwd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_16_WORD
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIwq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_16_WORD
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVIdw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_32_DOUBLEWORD
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIdd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_32_DOUBLEWORD
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIdq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_32_DOUBLEWORD
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVIqw(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UShort
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_16_WORD, EBCMoveTypes.BITS_64_QUADWORD
		)
		instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun MOVIqd(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: UInt
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_32_DOUBLEWORD, EBCMoveTypes.BITS_64_QUADWORD
		)
		instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun MOVIqq(
		operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?,
		immediate: ULong
	): EBCProcedure = this.also {
		MOVIBase(
			operand1, operand1Indirect, operand1Index,
			EBCImmediateDataLength.BITS_64_QUADWORD, EBCMoveTypes.BITS_64_QUADWORD
		)
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVnw(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand2: EBCRegisters, operand2Indirect: Boolean,
		operand1Index: UShort?, operand2Index: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put(
			(0x32 or (if (operand2Index != null) 0b01000000 else 0) or (if (operand1Index != null) 0b10000000 else 0))
				.toByte()
		)
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b00001000 else 0) or
					(operand2.ordinal shl 4) or (if (operand2Indirect) 0b10000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		if (operand2Index != null) instructionBuffer.putShort(operand2Index.toShort())
		addInstruction()
	}

	fun CALL32(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		relative: Boolean,
		native: Boolean,
		immediate: UInt?
	): EBCProcedure = this.also {
		instructionBuffer.put((0x03 or (if (immediate != null) (1 shl 7) else 0)).toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (if (relative) 0b10000 else 0)
					or (if (native) 0b100000 else 0)).toByte()
		)
		if (immediate != null) instructionBuffer.putInt(immediate.toInt())
		addInstruction()
	}

	fun POP32(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x2C or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun POP64(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x6C or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun POPn(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		immediate: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put((0x36 or (if (immediate != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (immediate != null) instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun PUSH32(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x2B or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun PUSH64(operand1: EBCRegisters, operand1Indirect: Boolean, operand1Index: UShort?): EBCProcedure = this.also {
		instructionBuffer.put((0x6B or (if (operand1Index != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		addInstruction()
	}

	fun PUSHn(
		operand1: EBCRegisters,
		operand1Indirect: Boolean,
		immediate: UShort?
	): EBCProcedure = this.also {
		instructionBuffer.put((0x35 or (if (immediate != null) 0b10000000 else 0)).toByte())
		instructionBuffer.put((operand1.ordinal or (if (operand1Indirect) 0b1000 else 0)).toByte())
		if (immediate != null) instructionBuffer.putShort(immediate.toShort())
		addInstruction()
	}

	fun RET(): EBCProcedure = this.also {
		instructionBuffer.put(0x04)
		instructionBuffer.put(0x00)
		addInstruction()
	}
}