package org.bread_experts_group.computer.arm.v4

import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.Processor
import org.bread_experts_group.computer.arm.v4.instruction.InstructionConditionalExecutionSuffix
import org.bread_experts_group.computer.arm.v4.register.Register
import org.bread_experts_group.computer.arm.v4.register.StatusRegister
import org.bread_experts_group.hex
import org.bread_experts_group.logging.ColoredHandler
import java.util.concurrent.CountDownLatch
import java.util.logging.Logger

// Thank you https://problemkaputt.de/gbatek.htm
// See IA-32 processor for base design
class ARMv4Processor : Processor {
	override lateinit var computer: Computer
	val registers = arrayOf(
		// Lo
		Register("r0", 0u),
		Register("r1", 0u),
		Register("r2", 0u),
		Register("r3", 0u),
		Register("r4", 0u),
		Register("r5", 0u),
		Register("r6", 0u),
		Register("r7", 0u),
		// Hi
		Register("r8", 0u),
		Register("r9", 0u),
		Register("r10", 0u),
		Register("r11", 0u),
		Register("r12", 0u),
		Register("r13", 0u),
		Register("r14", 0u),
		Register("r15/pc", 0u)
	)
	val pc = registers[15]

	val status = StatusRegister()
	val logger: Logger = ColoredHandler.newLoggerResourced("arm_v4_processor")

	override fun step() {
		this.prefetch()
		if (this.status.getFlag(StatusRegister.FlagType.THUMB_MODE)) this.decodeThumb(this.fetchThumb())
		else this.decodeArm(this.fetchArm())
	}

	fun fetchThumb(): UShort {
		return this.computer.requestMemoryAt16(this.pc.value.toULong()).also { this.pc.value += 2u }
	}

	fun fetchArm(): UInt {
		return this.computer.requestMemoryAt32(this.pc.value.toULong()).also { this.pc.value += 4u }
	}

	fun decodeThumb(fetched: UShort) {
		TODO(hex(fetched))
	}

	fun decodeArm(fetched: UInt) {
		val conditional = InstructionConditionalExecutionSuffix.mapping.getValue(fetched shr 28)
		val disassembly = StringBuilder("[${fetched.toString(2).padStart(32, '0')}:")
		val ok = when (conditional) {
			InstructionConditionalExecutionSuffix.EQ -> this.status.getFlag(StatusRegister.FlagType.ZERO)
			InstructionConditionalExecutionSuffix.NE -> !this.status.getFlag(StatusRegister.FlagType.ZERO)
			InstructionConditionalExecutionSuffix.CS -> this.status.getFlag(StatusRegister.FlagType.CARRY)
			InstructionConditionalExecutionSuffix.CC -> !this.status.getFlag(StatusRegister.FlagType.CARRY)
			InstructionConditionalExecutionSuffix.MI -> this.status.getFlag(StatusRegister.FlagType.NEGATIVE)
			InstructionConditionalExecutionSuffix.PL -> !this.status.getFlag(StatusRegister.FlagType.NEGATIVE)
			InstructionConditionalExecutionSuffix.VS -> this.status.getFlag(StatusRegister.FlagType.OVERFLOW)
			InstructionConditionalExecutionSuffix.VC -> !this.status.getFlag(StatusRegister.FlagType.OVERFLOW)
			InstructionConditionalExecutionSuffix.HI ->
				this.status.getFlag(StatusRegister.FlagType.CARRY) &&
						!this.status.getFlag(StatusRegister.FlagType.ZERO)

			InstructionConditionalExecutionSuffix.LS ->
				!this.status.getFlag(StatusRegister.FlagType.CARRY) ||
						this.status.getFlag(StatusRegister.FlagType.ZERO)

			InstructionConditionalExecutionSuffix.GE ->
				this.status.getFlag(StatusRegister.FlagType.NEGATIVE) ==
						this.status.getFlag(StatusRegister.FlagType.OVERFLOW)

			InstructionConditionalExecutionSuffix.LT ->
				this.status.getFlag(StatusRegister.FlagType.NEGATIVE) !=
						this.status.getFlag(StatusRegister.FlagType.OVERFLOW)

			InstructionConditionalExecutionSuffix.GT ->
				!this.status.getFlag(StatusRegister.FlagType.ZERO) &&
						(this.status.getFlag(StatusRegister.FlagType.NEGATIVE) ==
								this.status.getFlag(StatusRegister.FlagType.OVERFLOW))

			InstructionConditionalExecutionSuffix.LE ->
				this.status.getFlag(StatusRegister.FlagType.ZERO) ||
						(this.status.getFlag(StatusRegister.FlagType.NEGATIVE) !=
								this.status.getFlag(StatusRegister.FlagType.OVERFLOW))

			InstructionConditionalExecutionSuffix.AL -> true
		}
		if (!ok) {
			disassembly.append("✗]")
			return
		}
		disassembly.append("✓]")
		when ((fetched shr 25) and 0b111u) {
			0b001u -> {
				val checkA = (fetched shr 20) and 0b0000_0001_1011u
				if (checkA == 0b0000_0001_0010u) TODO("IMM STS REG")
				if (checkA == 0b0000_0001_0000u) TODO("UNDEF")
				val opcode = (fetched shr 21) and 0b1111u
				val setCond = (fetched shr 20) and 1u == 1u
				val rn = registers[((fetched shr 16) and 0b1111u).toInt()]
				val rd = registers[((fetched shr 12) and 0b1111u).toInt()]
				val rotate = (fetched shr 8) and 0b1111u
				val immediate = (fetched and 0b1111_1111u).rotateRight((rotate * 2u).toInt())
				if (setCond && rotate != 0u)
					this.status.setFlag(StatusRegister.FlagType.CARRY, (immediate shr 31) == 1u)
				when (opcode) {
					0b0000u -> TODO("AND")
					0b0001u -> TODO("EOR")
					0b0010u -> TODO("SUB")
					0b0011u -> TODO("RSB")
					0b0100u -> TODO("ADD")
					0b0101u -> TODO("ADC")
					0b0110u -> TODO("SBC")
					0b0111u -> TODO("RSC")
					0b1000u -> TODO("TST")
					0b1001u -> TODO("TEQ")
					0b1010u -> TODO("CMP")
					0b1011u -> TODO("CMN")
					0b1100u -> TODO("ORR")
					0b1101u -> {
						disassembly.append(" MOV ${rd.name}, #$immediate")
						rd.value = immediate
					}

					0b1110u -> TODO("BIC")
					0b1111u -> TODO("MVN")
				}
			}

			0b101u -> {
				val link = (fetched shr 24) and 1u == 1u
				if (link) TODO("BL")
				val offset = ((fetched and 0b1111_1111_1111_1111_1111_1111u) shl 8).toInt() shr 6
				disassembly.append(" B $offset")
				this.pc.value = (this.pc.value.toLong() + offset).toUInt()
			}

			else -> throw NotImplementedError(disassembly.toString())
		}
		logger.info(disassembly.toString())
	}

	val biosHooks: MutableMap<UInt, (ARMv4Processor) -> Unit> = mutableMapOf()
	var halt: CountDownLatch = CountDownLatch(1)
	fun prefetch() {
		this.halt.await()
		this.biosHooks[this.pc.value]?.invoke(this)
	}

	override fun reset() {
		this.registers.forEach { it.value = 0u }
		this.status.value = 0u
		halt.countDown()
	}
}