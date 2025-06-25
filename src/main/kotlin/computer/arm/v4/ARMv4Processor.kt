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
		Register("sp", 0u),
		Register("r14", 0u),
		Register("pc", 0u)
	)
	val pc = registers[15]
	val status = StatusRegister()
	val logger: Logger = ColoredHandler.newLoggerResourced("arm_v4_processor")

	override fun step() {
		this.prefetch()
		val log = if (this.status.getFlag(StatusRegister.FlagType.THUMB_MODE)) this.decodeThumb(this.fetchThumb())
		else this.decodeArm(this.fetchArm())
		logger.info(log)
	}

	fun fetchThumb(): UShort {
		return this.computer.requestMemoryAt16(this.pc.value.toULong()).also { this.pc.value += 2u }
	}

	fun fetchArm(): UInt {
		return this.computer.requestMemoryAt32(this.pc.value.toULong()).also { this.pc.value += 4u }
	}

	fun decodeThumb(fetched: UShort): String {
		TODO(hex(fetched))
	}

	fun decodeArm(fetched: UInt): String {
		val conditional = InstructionConditionalExecutionSuffix.mapping.getValue(fetched shr 28)
		val disassembly = StringBuilder(hex(this.pc.value - 4u))
		disassembly.append(":[")
		disassembly.append(fetched.toString(2).padStart(32, '0'))
		disassembly.append('/')
		disassembly.append(hex(fetched))
		disassembly.append(':')
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
			return disassembly.toString()
		}
		disassembly.append("✓]")
		when ((fetched shr 25) and 0b111u) {
			0b000u -> {
				val maskA = fetched and 0b00000001111100000000000011110000u
				if (maskA == 0b00000001001000000000000001110000u) TODO("Swf Brk $disassembly")
				if (maskA == 0b00000001011000000000000000010000u) TODO("Clz $disassembly")
				if (maskA == 0b00000001001000000000000000110000u) TODO("BL XCHG IS $disassembly")
				if (maskA == 0b00000001001000000000000000010000u) TODO("B XCHG IS $disassembly")
				val maskB = fetched and 0b00000001101100000000000011110000u
				if (maskB == 0b00000001001000000000000000000000u) {
					val statusBits = (fetched shr 16) and 0b1111u
					disassembly.append(" msr")
					disassembly.append(conditional.assemblerName)
					disassembly.append(" cpsr_")
					val disassemblyBits = StringBuilder()
					val transfer = registers[(fetched and 0b1111u).toInt()]
					for (mask in (0..24).reversed() step 8) {
						val statusBit = (statusBits shr (mask shr 3)) and 1u
						if (statusBit == 1u) {
							disassembly.append(
								when (mask) {
									24 -> 'f'
									16 -> 'x'
									8 -> 's'
									else -> 'c'
								}
							)
							val transferData = (transfer.value shr mask) and 0xFFu
							status.value = (status.value and (0xFFu shl mask).inv()) or (transferData shl mask)
							disassemblyBits.append(transferData.toString(2).padStart(8, '0'))
						} else disassemblyBits.append("x".repeat(8))
					}
					disassembly.append(", ")
					disassembly.append(transfer.name)
					disassembly.append(" (")
					disassembly.append(disassemblyBits)
					disassembly.append(')')
					return disassembly.toString()
				}
				if (maskB == 0b00000001000000000000000000000000u) TODO("Sts Reg to Reg")
				val maskC = fetched and 0b00000001100100000000000011110000u
				if (maskC == 0b00000001000000000000000001010000u) TODO("EDSP +/-")
				if (maskC == 0b00000001000000000000000010000000u) TODO("EDSP *")
				val maskD = fetched and 0b00000000000000000000000010010000u
				if (maskD == 0b00000000000000000000000010010000u) TODO("* / v/^")
				if (maskD == 0b00000000000000000000000000010000u) TODO("DP Reg")
				val maskE = fetched and 0b00000000000000000000000000010000u
				if (maskE == 0b00000000000000000000000000000000u) TODO("DP Imm")
				TODO("Delta")
			}

			0b001u -> {
				val checkA = (fetched shr 20) and 0b0000_0001_1011u
				if (checkA == 0b0000_0001_0010u) TODO("IMM STS REG")
				if (checkA == 0b0000_0001_0000u) TODO("UNDEF")
				val opcode = (fetched shr 21) and 0b1111u
				val s = (fetched shr 20) and 1u == 1u
				if (s) TODO("set cond")
				val rn = registers[((fetched shr 16) and 0b1111u).toInt()]
				val rd = registers[((fetched shr 12) and 0b1111u).toInt()]
				val rotate = (fetched shr 8) and 0b1111u
				val immediate = (fetched and 0b1111_1111u).rotateRight((rotate * 2u).toInt())
				when (opcode) {
					0b0000u -> TODO("AND")
					0b0001u -> TODO("EOR")
					0b0010u -> TODO("SUB")
					0b0011u -> TODO("RSB")
					0b0100u -> {
						disassembly.append(" add")
						disassembly.append(conditional.assemblerName)
						disassembly.append(' ')
						disassembly.append(rd.name)
						disassembly.append(", ")
						disassembly.append(rn.name)
						disassembly.append(", #")
						disassembly.append(hex(immediate))
						rd.value = rn.value + immediate
					}

					0b0101u -> TODO("ADC")
					0b0110u -> TODO("SBC")
					0b0111u -> TODO("RSC")
					0b1000u -> TODO("TST")
					0b1001u -> TODO("TEQ")
					0b1010u -> TODO("CMP")
					0b1011u -> TODO("CMN")
					0b1100u -> TODO("ORR")
					0b1101u -> {
						disassembly.append(" mov")
						disassembly.append(conditional.assemblerName)
						disassembly.append(' ')
						disassembly.append(rd.name)
						disassembly.append(", #")
						disassembly.append(hex(immediate))
						rd.value = immediate
					}

					0b1110u -> TODO("BIC")
					0b1111u -> TODO("MVN")
				}
			}

			0b010u -> {
				val rn = registers[((fetched shr 16) and 0b1111u).toInt()]
				val rd = registers[((fetched shr 12) and 0b1111u).toInt()]
				val p = (fetched shr 24) and 1u == 1u
				if (!p) TODO("post-index addressing")
				val u = (fetched shr 23) and 1u == 1u
				val b = (fetched shr 22) and 1u == 1u
				if (b) TODO("byte address")
				val w = (fetched shr 21) and 1u == 1u
				if (w) TODO("writeback")
				val l = (fetched shr 20) and 1u == 1u
				if (!l) TODO("store")
				val offset = fetched and 0b111111111111u
				val calculated = (if (u) UInt::plus else UInt::minus)(rn.value, offset)
				rd.value = computer.requestMemoryAt32(calculated.toULong())
				disassembly.append(" ldr")
				disassembly.append(conditional.assemblerName)
				disassembly.append(' ')
				disassembly.append(rd.name)
				disassembly.append(", ")
				disassembly.append('[')
				disassembly.append(rn.name)
				disassembly.append(' ')
				disassembly.append(if (u) '+' else '-')
				disassembly.append(" #")
				disassembly.append(hex(offset))
				disassembly.append(" (")
				disassembly.append(hex(calculated))
				disassembly.append(")] (")
				disassembly.append(hex(rd.value))
				disassembly.append(')')
			}

			0b101u -> {
				val link = (fetched shr 24) and 1u == 1u
				if (link) TODO("BL")
				val offset = (((fetched and 0b1111_1111_1111_1111_1111_1111u) shl 8).toInt() shr 6) + 4
				disassembly.append(" b")
				disassembly.append(conditional.assemblerName)
				disassembly.append(" #")
				disassembly.append(hex(offset))
				disassembly.append(" (")
				this.pc.value = (this.pc.value.toLong() + offset).toUInt()
				disassembly.append(hex(this.pc.value))
				disassembly.append(')')
			}

			else -> throw NotImplementedError(disassembly.toString())
		}
		return disassembly.toString()
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