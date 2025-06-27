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
		Register("lr", 0u),
		Register("pc", 0u)
	)
	val lr = registers[14]
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
		return this.computer.requestMemoryAt16(this.pc.value.toULong() - 4u).also { this.pc.value += 2u }
	}

	fun fetchArm(): UInt {
		return this.computer.requestMemoryAt32(this.pc.value.toULong() - 8u).also { this.pc.value += 4u }
	}

	fun checkConditional(cond: InstructionConditionalExecutionSuffix) = when (cond) {
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

	fun carryFrom(n: UInt, m: UInt) = n.toULong() + m > UInt.MAX_VALUE
	fun overflowFrom(n: UInt, m: UInt): Boolean =
		(((n xor m) and 0x80000000u) == 0u) &&
				(((n xor (n + m)) and 0x80000000u) != 0u)

	fun decodeThumb(fetched: UShort): String {
		val disassembly = StringBuilder(hex(this.pc.value - 2u))
		disassembly.append(":[")
		disassembly.append(fetched.toString(2).padStart(16, '0'))
		disassembly.append('/')
		disassembly.append(hex(fetched))
		disassembly.append(']')
		val extFetched = fetched.toUInt()
		when ((extFetched shr 13) and 0b111u) {
			0b000u -> {
				val maskA = extFetched and 0b0001110000000000u
				if (maskA == 0b0001100000000000u) {
					TODO("Add/sub reg $disassembly")
				}
				if (maskA == 0b0001110000000000u) {
					val rd = registers[(extFetched and 0b111u).toInt()]
					val rn = registers[((extFetched shr 3) and 0b111u).toInt()]
					val imm = (extFetched shr 6) and 0b111u
					if ((maskA shr 9) and 1u == 1u) TODO("!!! $disassembly")
					disassembly.append(" add ")
					disassembly.append(rd.name)
					disassembly.append(", ")
					disassembly.append(rn.name)
					disassembly.append(", #")
					disassembly.append(hex(imm))
					rd.value = rn.value + imm
					status.setFlag(StatusRegister.FlagType.NEGATIVE, rd.value and 0x80000000u != 0u)
					status.setFlag(StatusRegister.FlagType.ZERO, rd.value == 0u)
					status.setFlag(StatusRegister.FlagType.CARRY, carryFrom(rn.value, imm))
					status.setFlag(StatusRegister.FlagType.OVERFLOW, overflowFrom(rn.value, imm))
					return disassembly.toString()
				}
				if ((extFetched shr 11) and 0b11u != 0b00u) TODO("!*! $disassembly")
				val imm = ((extFetched shr 6) and 0b11111u).toInt()
				val rm = registers[((extFetched shr 3) and 0b111u).toInt()]
				val rd = registers[(extFetched and 0b111u).toInt()]
				disassembly.append(" lsl ")
				disassembly.append(rd.name)
				disassembly.append(", ")
				disassembly.append(rm.name)
				disassembly.append(", ")
				disassembly.append('#')
				disassembly.append(hex(imm))
				if (imm != 0)
					status.setFlag(StatusRegister.FlagType.CARRY, (rm.value and (1u shl (32 - imm))) != 0u)
				rd.value = rm.value shl imm
				status.setFlag(StatusRegister.FlagType.NEGATIVE, rd.value and 0x80000000u != 0u)
				status.setFlag(StatusRegister.FlagType.ZERO, rd.value == 0u)
				return disassembly.toString()
			}

			0b001u -> {
				val rdn = registers[((extFetched shr 8) and 0b111u).toInt()]
				val imm = extFetched and 0b11111111u
				when ((extFetched shr 11) and 0b11u) {
					0b00u -> {
						disassembly.append(" mov ")
						disassembly.append(rdn.name)
						disassembly.append(", #")
						disassembly.append(hex(imm))
						rdn.value = imm
						status.setFlag(StatusRegister.FlagType.NEGATIVE, imm and 0x80000000u != 0u)
						status.setFlag(StatusRegister.FlagType.ZERO, imm == 0u)
					}

					else -> TODO("ASCM Imm, $rdn, $imm, $disassembly")
				}
				return disassembly.toString()
			}

			0b010u -> {
				val maskA = extFetched and 0b0001111100000000u
				if (maskA == 0b0000011100000000u) TODO("BX/XCHG $disassembly")
				val maskB = extFetched and 0b0001110000000000u
				if (maskB == 0b0000000000000000u) TODO("DP Reg $disassembly")
				if (maskB == 0b0000010000000000u) {
					val rm = registers[(((extFetched shr 3) and 0b111u) or ((extFetched shr 3) and 0b1000u)).toInt()]
					val rd = registers[((extFetched and 0b111u) or ((extFetched shr 4) and 0b1000u)).toInt()]
					when ((extFetched shr 8) and 0b11u) {
						0b10u -> {
							disassembly.append(" mov ")
							disassembly.append(rd.name)
							disassembly.append(", ")
							disassembly.append(rm.name)
							rd.value = rm.value
						}

						else -> TODO("SpDP $disassembly")
					}
					return disassembly.toString()
				}
				val maskC = extFetched and 0b0001100000000000u
				if (maskC == 0b0000100000000000u) {
					disassembly.append(" ldr ")
					val rd = registers[((extFetched shr 8) and 0b111u).toInt()]
					disassembly.append(rd.name)
					disassembly.append(", ")
					val imm8 = (extFetched and 0b11111111u) * 4u
					disassembly.append("[pc, #")
					disassembly.append(hex(imm8))
					disassembly.append("] (")
					val addr = pc.value + imm8
					disassembly.append(hex(addr))
					disassembly.append(") (")
					rd.value = computer.requestMemoryAt32(addr.toULong())
					disassembly.append(hex(rd.value))
					disassembly.append(')')
					return disassembly.toString()
				}
				val maskD = extFetched and 0b0001000000000000u
				if (maskD == 0b0001000000000000u) TODO("LR Reg $disassembly")
				TODO("Delta $disassembly")
			}

			0b011u -> {
				if ((extFetched shr 11) and 0b11u != 0b00u) TODO("!*! $disassembly")
				val imm = (extFetched shr 6) and 0b11111u
				val rn = registers[((extFetched shr 3) and 0b111u).toInt()]
				val rd = registers[(extFetched and 0b111u).toInt()]
				disassembly.append(" str ")
				disassembly.append(rd.name)
				disassembly.append(", ")
				disassembly.append('[')
				disassembly.append(rn.name)
				disassembly.append(" + #")
				disassembly.append(hex(imm))
				disassembly.append(" * 4 (")
				val addr = (imm * 4u) + rn.value
				disassembly.append(hex(addr))
				disassembly.append(")] (")
				disassembly.append(hex(rd.value))
				disassembly.append(')')
				computer.setMemoryAt32(addr.toULong(), rd.value)
				return disassembly.toString()
			}

			0b110u -> {
				val maskA = extFetched and 0b0001111100000000u
				if (maskA == 0b0001111100000000u) TODO("Int $disassembly")
				val maskB = extFetched and 0b0001000000000000u
				if (maskB == 0b0001000000000000u) {
					disassembly.append(" b")
					val condition = InstructionConditionalExecutionSuffix.mapping.getValue(
						(extFetched shr 8) and 0b1111u
					)
					disassembly.append(condition.assemblerName)
					val imm = (extFetched and 0b11111111u).toByte().toInt() shl 1
					disassembly.append(" #")
					disassembly.append(hex(imm))
					if (checkConditional(condition)) pc.value = (pc.value.toInt() + imm).toUInt()
					return disassembly.toString()
				}
				if (maskB == 0b0000000000000000u) TODO("ls $disassembly")
				TODO("Delta $disassembly")
			}

			0b111u -> {
				if ((extFetched shr 11) and 0b11u != 0b10u) TODO("bad first half $disassembly")
				lr.value = pc.value + ((extFetched and 0b11111111111u) shl 12)

				val secondHalf = fetchThumb()
				disassembly.append(":[${hex(secondHalf)}]")
				val secondHalfExt = secondHalf.toUInt()
				val secondH = (secondHalfExt shr 11) and 0b11u
				if (secondH == 0b11u) {
					disassembly.append(" b ")
					val savedPC = pc.value
					pc.value = lr.value + ((secondHalfExt and 0b11111111111u) shl 1)
					lr.value = savedPC or 1u
					disassembly.append('#')
					disassembly.append(hex(pc.value))
					return disassembly.toString()
				}
				if (secondH == 0b01u) TODO("BLX $disassembly")
				TODO("Delta $disassembly")
			}

			else -> throw NotImplementedError(disassembly.toString())
		}
	}

	fun decodeArm(fetched: UInt): String {
		val disassembly = StringBuilder(hex(this.pc.value - 4u))
		disassembly.append(":[")
		disassembly.append(fetched.toString(2).padStart(32, '0'))
		disassembly.append('/')
		disassembly.append(hex(fetched))
		disassembly.append(':')
		val conditional = InstructionConditionalExecutionSuffix.mapping.getValue(fetched shr 28)
		if (!checkConditional(conditional)) {
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
				if (maskA == 0b00000001001000000000000000010000u) {
					val target = registers[(fetched and 0b1111u).toInt()]
					disassembly.append(" bx")
					disassembly.append(conditional.assemblerName)
					disassembly.append(' ')
					disassembly.append(target.name)
					disassembly.append(" (")
					disassembly.append(hex(target.value))
					disassembly.append(')')
					val instruction = target.value
					status.setFlag(StatusRegister.FlagType.THUMB_MODE, instruction and 1u == 1u)
					pc.value = instruction and 0xFFFFFFFEu
					return disassembly.toString()
				}
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