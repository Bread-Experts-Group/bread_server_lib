package org.bread_experts_group.api.compile.ebc

import org.bread_experts_group.normalize
import java.nio.ByteBuffer
import java.nio.ByteOrder

// RETURN VALUES: R7 (8Bytes or less)
// PARAMETERS: STACK
// R0 STACK POINTER
// R1 / R2 / R3 CALL PRESERVED
// R4 / R5 / R6 / R7 NOT PRESERVED
@Suppress("FunctionName")
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
			return (encoded or ((naturalBits / 2u) shl 12) or
					(constantUnits shl naturalBits.toInt()) or naturalUnits).toUShort()
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

	fun MOVI(
		operand1: EBCRegisters, operand1Indirect: Boolean,
		operand1Index: UShort?,
		move: EBCMoveTypes, immediate: ULong
	): EBCProcedure = this.also {
		instructionBuffer.put(0xF7.toByte())
		instructionBuffer.put(
			(operand1.ordinal or (if (operand1Indirect) 0b1000 else 0) or (move.ordinal shl 4)
					or (if (operand1Index != null) 0b1000000 else 0)).toByte()
		)
		if (operand1Index != null) instructionBuffer.putShort(operand1Index.toShort())
		instructionBuffer.putLong(immediate.toLong())
		addInstruction()
	}

	fun MOVn(
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

	fun CALL(
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