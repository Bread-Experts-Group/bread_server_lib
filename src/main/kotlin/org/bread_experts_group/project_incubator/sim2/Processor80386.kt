package org.bread_experts_group.project_incubator.sim2

import org.bread_experts_group.generic.io.reader.DirectDataSink
import org.bread_experts_group.generic.io.reader.DirectDataSource
import org.bread_experts_group.generic.logging.LevelLogger
import org.bread_experts_group.generic.logging.LogMessage
import kotlin.experimental.and
import kotlin.experimental.or

@Suppress("PropertyName")
class Processor80386(
	val memorySource: DirectDataSource<UInt>,
	val memorySink: DirectDataSink<UInt>
) {
	interface Flags : Register16 {
		var CARRY: Boolean
			get() = this.d and 0b1 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b1
				else this.d and ((0b1.inv() and 0xFFFF).toShort())
			}
		var PARITY: Boolean
			get() = this.d and 0b100 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b100
				else this.d and ((0b100.inv() and 0xFFFF).toShort())
			}
		var ADJUST: Boolean
			get() = this.d and 0b10000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b10000
				else this.d and ((0b10000.inv() and 0xFFFF).toShort())
			}
		var ZERO: Boolean
			get() = this.d and 0b1000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b1000000
				else this.d and ((0b1000000.inv() and 0xFFFF).toShort())
			}
		var SIGN: Boolean
			get() = this.d and 0b10000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b10000000
				else this.d and ((0b10000000.inv() and 0xFFFF).toShort())
			}
		var TRAP_FLAG: Boolean
			get() = this.d and 0b100000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b100000000
				else this.d and ((0b100000000.inv() and 0xFFFF).toShort())
			}
		var INTERRUPT_ENABLE: Boolean
			get() = this.d and 0b1000000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b1000000000
				else this.d and ((0b1000000000.inv() and 0xFFFF).toShort())
			}
		var DIRECTION: Boolean
			get() = this.d and 0b10000000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b10000000000
				else this.d and ((0b10000000000.inv() and 0xFFFF).toShort())
			}
		var OVERFLOW: Boolean
			get() = this.d and 0b100000000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b100000000000
				else this.d and ((0b100000000000.inv() and 0xFFFF).toShort())
			}
		var IO_PRIVILEGE_LEVEL: Byte
			get() = ((this.d and 0b11000000000000) and 0xFF).toByte()
			set(value) {
				this.d = (this.d and ((0b11000000000000.inv() and 0xFFFF).toShort())) or
						(value.toInt() shl 12).toShort()
			}
		var NESTED_TASK_FLAG: Boolean
			get() = this.d and 0b100000000000000 != 0.toShort()
			set(value) {
				this.d = if (value) this.d or 0b100000000000000
				else this.d and ((0b100000000000000.inv() and 0xFFFF).toShort())
			}
	}

	class EFlags : Register32, Flags {
		override val label: String = "eflags"
		override var q: Int = 0x00000002
			get() = field and 0b00000000_00000011_01111111_11010101

		var RESUME_FLAG: Boolean
			get() = this.q and 0b10000000000000000 != 0
			set(value) {
				this.q = if (value) this.q or 0b10000000000000000
				else this.q and (0b10000000000000000.inv())
			}
		var VIRTUAL_8086_MODE: Boolean
			get() = this.q and 0b100000000000000000 != 0
			set(value) {
				this.q = if (value) this.q or 0b100000000000000000
				else this.q and (0b100000000000000000.inv())
			}
	}

	class ControlRegister0 : Register32 {
		override val label: String = "cr0"
		override var q: Int = 0
			get() = field and 0b10000000_00000000_00000000_00011111.toInt()
			set(value) {
				field = value and (0b10000000_00000000_00000000_00011111.toInt())
			}

		var PROTECTION_ENABLE: Boolean
			get() = this.q and 0b1 != 0
			set(value) {
				this.q = if (value) this.q or 0b1
				else this.q and (0b1.inv())
			}
		var MATH_PRESENT: Boolean
			get() = this.q and 0b10 != 0
			set(value) {
				this.q = if (value) this.q or 0b10
				else this.q and (0b10.inv())
			}
		var EMULATION: Boolean
			get() = this.q and 0b100 != 0
			set(value) {
				this.q = if (value) this.q or 0b100
				else this.q and (0b100.inv())
			}
		var TASK_SWITCHED: Boolean
			get() = this.q and 0b1000 != 0
			set(value) {
				this.q = if (value) this.q or 0b1000
				else this.q and (0b1000.inv())
			}
		var EXTENSION_TYPE: Boolean
			get() = this.q and 0b10000 != 0
			set(value) {
				this.q = if (value) this.q or 0b10000
				else this.q and (0b10000.inv())
			}
		var PAGING: Boolean
			get() = this.q and 0b10000000_00000000_00000000_00000000.toInt() != 0
			set(value) {
				this.q = if (value) this.q or 0b10000000_00000000_00000000_00000000.toInt()
				else this.q and (0b10000000_00000000_00000000_00000000.toInt().inv())
			}
	}

	val CR0: ControlRegister0 = ControlRegister0() // RESET: bits 30 .. 5 undefined
	val CR2: Register32.Basic = Register32.Basic(0, "cr2")
	val CR3: Register32.Basic = Register32.Basic(0, "cr3")

	val EAX: Register32.Basic = Register32.Basic(0, "eax") // RESET: non-zero if faulty
	val AX: Register16.Basic = EAX.AliasL16("ax")
	val AH: Register8.Basic = AX.AliasH8("ah")
	val AL: Register8.Basic = AX.AliasL8("al")
	val EBX: Register32.Basic = Register32.Basic(0, "ebx") // RESET: undefined
	val BX: Register16.Basic = EBX.AliasL16("bx")
	val BH: Register8.Basic = BX.AliasH8("bh")
	val BL: Register8.Basic = BX.AliasL8("bl")
	val ECX: Register32.Basic = Register32.Basic(0, "ecx") // RESET: undefined
	val CX: Register16.Basic = ECX.AliasL16("cx")
	val CH: Register8.Basic = CX.AliasH8("ch")
	val CL: Register8.Basic = CX.AliasL8("cl")
	val EDX: Register32.Basic = Register32.Basic(0x3_0, "edx") // RESET: upper 16 undefined
	val DX: Register16.Basic = EDX.AliasL16("dx")
	val DH: Register8.Basic = DX.AliasH8("dh") // RESET: device ID
	val DL: Register8.Basic = DX.AliasL8("dl") // RESET: stepping ID
	val EBP: Register32.Basic = Register32.Basic(0, "ebp") // RESET: undefined
	val BP: Register16.Basic = EBP.AliasL16("bp")
	val ESP: Register32.Basic = Register32.Basic(0, "esp") // RESET: undefined
	val SP: Register16.Basic = ESP.AliasL16("sp")
	val ESI: Register32.Basic = Register32.Basic(0, "esi") // RESET: undefined
	val SI: Register16.Basic = ESI.AliasL16("si")
	val EDI: Register32.Basic = Register32.Basic(0, "edi") // RESET: undefined
	val DI: Register16.Basic = EDI.AliasL16("di")

	class SegmentRegister(d: Short, label: String, var base: UInt = 0u) : Register16.Basic(d, label) {
		var csD = false // in use by CS: 0=operand/address 16-bit, 1=operand/address 32-bit
		var ssB = false // in use by SS: 0=stack address 16-bit, 1=stack address 32-bit
	}

	val CS: SegmentRegister = SegmentRegister(0x000, "cs", 0xFFFF0000u)
	val SS: SegmentRegister = SegmentRegister(0x0000, "ss")
	val DS: SegmentRegister = SegmentRegister(0x0000, "ds")
	val ES: SegmentRegister = SegmentRegister(0x0000, "es")
	val FS: SegmentRegister = SegmentRegister(0x0000, "fs")
	val GS: SegmentRegister = SegmentRegister(0x0000, "gs")

	val IDTR_LIMIT = Register16.Basic(0x03FF, "idtr.limit")
	val IDTR_BASE = Register32.Basic(0, "idtr.base")

	val GDTR_LIMIT = Register16.Basic(0x03FF, "idtr.limit")
	val GDTR_BASE = Register32.Basic(0, "idtr.base")

	val EFLAGS: EFlags = EFlags()
	val FLAGS: Flags = EFLAGS
	val EIP: Register32.Basic = Register32.Basic(0x0000FFF0, "eip")
	val IP: Register16.Basic = EIP.AliasL16("ip")

	val logger = LevelLogger<LogMessage>("80386")

	fun getSegmentDetails(selector: UShort): SelectorInformation {
		val rpl = (selector and 0b111u).toUByte()
		val ldt = selector and 0b1000u != 0u.toUShort()
		val index = selector.toUInt() shr 4
		if (index == 0u) return SelectorInformation(null, rpl)
		// TODO: Add GDTR_LIMIT check
		// TODO: Add index = 0 check
		// TODO: LDT
		val segmentLocation = this.GDTR_BASE.qu + (8u * index)
		val segment1 = this.memorySource.readU32K(segmentLocation)
		val segment2 = this.memorySource.readU32K(segmentLocation + 4u)
		val base = (segment2 and 0xFF000000u) or ((segment2 and 0xFFu) shl 16) or (segment1 shr 16)
		val limit = ((segment2 shr 16) and 0b1111u) or (segment1 and 0xFFFFu)
		val type = (segment2 shr 8) and 0b11111u
		val dpl = ((segment2 shr 13) and 0b11u).toUByte()
		val present = (segment2 shr 15) and 0b1u == 1u
		val avl = (segment2 shr 20) and 0b1u == 1u
		val db = (segment2 shr 22) and 0b1u == 1u
		val granularity = (segment2 shr 23) and 0b1u == 1u
		return SelectorInformation(
			if (type and 0b10000u != 0u) {
				if (type and 0b1000u != 0u) SegmentInformation.Code(
					base, limit, dpl, present, avl, db, granularity,
					type and 0b100u != 0u,
					type and 0b10u != 0u,
					type and 0b1u != 0u
				) else SegmentInformation.Data(
					base, limit, dpl, present, avl, db, granularity,
					type and 0b100u != 0u,
					type and 0b10u != 0u,
					type and 0b1u != 0u
				)
			} else TODO("Sys"),
			rpl
		)
	}

	fun ipAndAdvance(n: Int): UInt {
		val ip = this.IP.du
		this.IP.du = (ip + n.toUShort()).toUShort()
		return CS.base + ip.toUInt()
	}

	fun instructionReadU8I(): Int {
		val ip = ipAndAdvance(1)
		return memorySource.readU8I(ip)
	}

	fun instructionReadS8(): Byte {
		val ip = ipAndAdvance(1)
		return memorySource.readS8(ip)
	}

	fun instructionReadU8K(): UByte {
		val ip = ipAndAdvance(1)
		return memorySource.readU8K(ip)
	}

	fun instructionReadU16K(): UShort {
		val ip = ipAndAdvance(2)
		return memorySource.readU16K(ip)
	}

	fun instructionReadS16(): Short {
		val ip = ipAndAdvance(2)
		return memorySource.readS16(ip)
	}

	fun instructionReadS32(): Int {
		val ip = ipAndAdvance(4)
		return memorySource.readS32(ip)
	}

	fun instructionReadU32K(): UInt {
		val ip = ipAndAdvance(4)
		return memorySource.readU32K(ip)
	}

	val stackAddress32: Boolean
		get() = if (this.CR0.PROTECTION_ENABLE && !this.EFLAGS.VIRTUAL_8086_MODE)
//			if (this.operandSizeOverride) !this.SS.ssB else this.SS.ssB
			this.SS.ssB // TODO: pending stack address override
		else false

	fun popU16K(): UShort {
		if (stackAddress32) {
			val p = this.memorySource.readU16K(this.SS.base + this.ESP.qu)
			this.ESP.qu += 2u
			return p
		} else {
			val p = this.memorySource.readU16K(this.SS.base + this.SP.du)
			this.SP.du = (this.SP.du + 2u).toUShort()
			return p
		}
	}

	fun popU32K(): UInt {
		if (stackAddress32) {
			val p = this.memorySource.readU32K(this.SS.base + this.ESP.qu)
			this.ESP.qu += 4u
			return p
		} else {
			val p = this.memorySource.readU32K(this.SS.base + this.SP.du)
			this.SP.du = (this.SP.du + 4u).toUShort()
			return p
		}
	}

	fun pushU16K(n: UShort) {
		if (stackAddress32) {
			this.ESP.qu -= 2u
			this.memorySink.writeU16K(this.SS.base + this.ESP.qu, n)
		} else {
			this.SP.du = (this.SP.du - 2u).toUShort()
			this.memorySink.writeU16K(this.SS.base + this.SP.du, n)
		}
	}

	fun pushU32K(n: UInt) {
		if (stackAddress32) {
			this.ESP.qu -= 4u
			this.memorySink.writeU32K(this.SS.base + this.ESP.qu, n)
		} else {
			this.SP.du = (this.SP.du - 4u).toUShort()
			this.memorySink.writeU32K(this.SS.base + this.SP.du, n)
		}
	}

	// TODO: CS D-bit 0=O/A16 1=O/A32
	// TODO: SS B-bit 0=SA16 1=SA32
	var addressSizeOverride = false
	val address32: Boolean
		get() = if (this.CR0.PROTECTION_ENABLE && !this.EFLAGS.VIRTUAL_8086_MODE)
			if (this.operandSizeOverride) !this.CS.csD else this.CS.csD
		else this.addressSizeOverride

	var operandSizeOverride = false
	val operand32: Boolean
		get() = if (this.CR0.PROTECTION_ENABLE && !this.EFLAGS.VIRTUAL_8086_MODE)
			if (this.operandSizeOverride) !this.CS.csD else this.CS.csD
		else this.operandSizeOverride

	var segmentOverride: SegmentRegister? = null
	var interrupt: Int? = null
	var interruptLastIP: UShort = 0u

	enum class RepeatType {
		UNCONDITIONAL,
		ZERO,
		NOT_ZERO
	}

	data class RepeatingInstruction(
		val type: RepeatType,
		val instruction: Instruction
	)

	var repeating: RepeatingInstruction? = null

	fun step() {
		var repAddressing: Register? = null
		if (repeating != null) {
			repAddressing = if (address32) this.ECX else this.CX
			if (
				(repAddressing is Register32 && repAddressing.qu == 0u) ||
				repAddressing.du == 0u.toUShort()
			) this.repeating = null
		}
		val intr = interrupt
		if (intr != null) {
			val vector = this.memorySource.readU32K(IDTR_BASE.qu + (4u * intr.toUInt()))
			val vCS = vector shr 16
			val vIP = vector and 0xFFFFu
			pushU16K(this.FLAGS.du)
			this.FLAGS.INTERRUPT_ENABLE = false
			// TODO clear Trap Flag
			pushU16K(this.CS.du)
			pushU16K(interruptLastIP)
			this.CS.du = vCS.toUShort()
			this.CS.base = this.CS.du.toUInt() shl 4
			this.IP.du = vIP.toUShort()
			this.interrupt = null
			return
		}
		val rep = repeating
		if (rep != null) {
			rep.instruction.execute(this)
			if (repAddressing is Register32) repAddressing.qu--
			else (repAddressing as Register16).du--
			when (rep.type) {
				RepeatType.ZERO if !this.FLAGS.ZERO -> this.repeating = null
				RepeatType.NOT_ZERO if this.FLAGS.ZERO -> this.repeating = null
				else -> {}
			}
			return
		}
		interruptLastIP = this.IP.du
		when (val opcode = instructionReadU8I()) {
			0x01 -> AddInstruction.ModRM1632Reg.execute(this)
			0x05 -> AddInstruction.EAXmm1632.execute(this)
			0x08 -> LogicalInclusiveORInstruction.ModRM8Imm8.execute(this)
			0x0D -> LogicalInclusiveORInstruction.EAXImm1632.execute(this)
			0x0F -> when (val opcode = instructionReadU8I()) {
				0x01 -> {
					val (mod, sel, rm) = decompose233(this.instructionReadS8())
					when (sel) {
						2 -> LoadGlobalDescriptorTableRegister(mod, rm)
						3 -> LoadInterruptDescriptorTableRegister(mod, rm)
						else -> TODO("Selector $sel")
					}.execute(this)
				}

				0x20 -> MoveInstruction.ControlRegisterToRegister.execute(this)
				0x22 -> MoveInstruction.RegisterToControlRegister.execute(this)
				0x80 -> JumpNearIfConditionIsMet.Overflow.execute(this)
				0x81 -> JumpNearIfConditionIsMet.NotOverflow.execute(this)
				0x82 -> JumpNearIfConditionIsMet.Carry.execute(this)
				0x83 -> JumpNearIfConditionIsMet.NotCarry.execute(this)
				0x84 -> JumpNearIfConditionIsMet.Zero.execute(this)
				0x85 -> JumpNearIfConditionIsMet.NotZero.execute(this)
				0x86 -> JumpNearIfConditionIsMet.NotAbove.execute(this)
				0x87 -> JumpNearIfConditionIsMet.Above.execute(this)
				0x88 -> JumpNearIfConditionIsMet.Sign.execute(this)
				0x89 -> JumpNearIfConditionIsMet.NotSign.execute(this)
				0x8A -> JumpNearIfConditionIsMet.Parity.execute(this)
				0x8B -> JumpNearIfConditionIsMet.NotParity.execute(this)
				0x8C -> JumpNearIfConditionIsMet.Less.execute(this)
				0x8D -> JumpNearIfConditionIsMet.NotLess.execute(this)
				0x8E -> JumpNearIfConditionIsMet.LessOrEqual.execute(this)
				0x8F -> JumpNearIfConditionIsMet.Greater.execute(this)
				0xB2 -> LoadFullPointer.Basic(this.SS, 'S').execute(this)
				0xB4 -> LoadFullPointer.Basic(this.FS, 'F').execute(this)
				0xB5 -> LoadFullPointer.Basic(this.GS, 'G').execute(this)

				else -> {
					var error = "invalid opcode (0f ${opcode.toUByte().toHexString()}) "
					error += "@ ${CS.base.toHexString()}:${(EIP.q.toUInt() - 1u).toHexString()}"
					TODO(error)
				}
			}

			0x25 -> LogicalANDInstruction.EAXImm1632.execute(this)

			0x26 -> {
				this.segmentOverride = this.ES
				return
			}

			0x2E -> {
				this.segmentOverride = this.CS
				return
			}

			0x31 -> LogicalExclusiveORInstruction.ModRMRegister.Bit1632.execute(this)
			0x36 -> {
				this.segmentOverride = this.SS
				return
			}

			0x38 -> CompareTwoOperandsInstruction.ModRM8Reg8.execute(this)
			0x39 -> CompareTwoOperandsInstruction.ModRMRegister1632.execute(this)
			0x3C -> CompareTwoOperandsInstruction.ALImm8.execute(this)
			0x3D -> CompareTwoOperandsInstruction.EAXImm1632.execute(this)
			0x3E -> {
				this.segmentOverride = this.DS
				return
			}

			in 0x40..0x47 -> IncrementBy1Instruction.Register(opcode - 0x40).execute(this)
			0x60 -> PushAllGeneralRegistersInstruction.execute(this)
			0x61 -> PopAllGeneralRegistersInstruction.execute(this)
			0x66 -> {
				this.operandSizeOverride = true
				return
			}

			0x67 -> {
				this.addressSizeOverride = true
				return
			}

			0x70 -> JumpShortIfConditionIsMetInstruction.Overflow.execute(this)
			0x71 -> JumpShortIfConditionIsMetInstruction.NotOverflow.execute(this)
			0x72 -> JumpShortIfConditionIsMetInstruction.Carry.execute(this)
			0x73 -> JumpShortIfConditionIsMetInstruction.NotCarry.execute(this)
			0x74 -> JumpShortIfConditionIsMetInstruction.Zero.execute(this)
			0x75 -> JumpShortIfConditionIsMetInstruction.NotZero.execute(this)
			0x76 -> JumpShortIfConditionIsMetInstruction.NotAbove.execute(this)
			0x77 -> JumpShortIfConditionIsMetInstruction.Above.execute(this)
			0x78 -> JumpShortIfConditionIsMetInstruction.Sign.execute(this)
			0x79 -> JumpShortIfConditionIsMetInstruction.NotSign.execute(this)
			0x7A -> JumpShortIfConditionIsMetInstruction.Parity.execute(this)
			0x7B -> JumpShortIfConditionIsMetInstruction.NotParity.execute(this)
			0x7C -> JumpShortIfConditionIsMetInstruction.Less.execute(this)
			0x7D -> JumpShortIfConditionIsMetInstruction.NotLess.execute(this)
			0x7E -> JumpShortIfConditionIsMetInstruction.LessOrEqual.execute(this)
			0x7F -> JumpShortIfConditionIsMetInstruction.Greater.execute(this)
			0x80 -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					4 -> LogicalANDInstruction.ModRM8Imm8(mod, rm)
					7 -> CompareTwoOperandsInstruction.ModRM8Imm8(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			0x81 -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					1 -> LogicalInclusiveORInstruction.ModRM1632Imm1632(mod, rm)
					7 -> CompareTwoOperandsInstruction.ModRM1632Imm1632(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			0x83 -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					0 -> AddInstruction.ModRM1632Imm1632(mod, rm)
					5 -> IntegerSubtractionInstruction.ModRM1632Imm8(mod, rm)
					7 -> CompareTwoOperandsInstruction.ModRM1632Imm8(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			0x87 -> ExchangeRMWithRegisterInstruction.Bit1632.execute(this)
			0x88 -> MoveInstruction.RegisterToModRm.Bit8.execute(this)
			0x89 -> MoveInstruction.RegisterToModRm.Bit1632.execute(this)
			0x8C -> MoveInstruction.FromSegment.execute(this)
			0x8E -> MoveInstruction.ToSegment.execute(this)
			0x9A -> CallProcedureInstruction.Gate.execute(this)
			0x9E -> StoreAHIntoFlagsInstruction.execute(this)
			0xA4 -> MoveDataFromStringToStringInstruction.Bit8.execute(this)
			0xA5 -> MoveDataFromStringToStringInstruction.Bit1632.execute(this)
			0xA6 -> CompareStringOperandsInstruction.Bit8.execute(this)
			0xA7 -> CompareStringOperandsInstruction.Bit1632.execute(this)
			0xA8 -> LogicalCompareInstruction.ALImm8.execute(this)
			0xA9 -> LogicalCompareInstruction.EAXImm1632.execute(this)
			0xAA -> StoreStringDataInstruction.Bit8.execute(this)
			0xAB -> StoreStringDataInstruction.Bit1632.execute(this)
			0xAC -> LoadStringOperandInstruction.Bit8.execute(this)
			0xAD -> LoadStringOperandInstruction.Bit1632.execute(this)
			0xAE -> CompareStringDataInstruction.Bit8.execute(this)
			0xAF -> CompareStringDataInstruction.Bit1632.execute(this)
			in 0xB0..0xB7 -> MoveInstruction.ImmediateToRegister.Bit8(opcode - 0xB0).execute(this)
			in 0xB8..0xBF -> MoveInstruction.ImmediateToRegister.Bit1632(opcode - 0xB8).execute(this)
			0xC1 -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					4 -> ShiftInstruction.Left.ModRM1632Imm8(mod, rm)
					5 -> ShiftInstruction.Right.ModRM1632Imm8(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			0xC3 -> ReturnFromProcedureInstruction.Near.execute(this)
			0xC4 -> LoadFullPointer.Basic(this.ES, 'E').execute(this)
			0xC5 -> LoadFullPointer.Basic(this.DS, 'D').execute(this)
			0xC7 -> MoveInstruction.ImmediateToModRm.Bit1632.execute(this)
			0xCB -> ReturnFromProcedureInstruction.Far.execute(this)
			0xD0 -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					4 -> ShiftInstruction.Left.Once(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			0xE0 -> LoopControlWithCXCounterInstruction.NotZero.execute(this)
			0xE1 -> LoopControlWithCXCounterInstruction.Zero.execute(this)
			0xE2 -> LoopControlWithCXCounterInstruction.Unconditional.execute(this)
			0xE3 -> JumpShortIfConditionIsMetInstruction.CXIs0.execute(this)
			0xE8 -> CallProcedureInstruction.Near.execute(this)
			0xE9 -> JumpInstruction.Near.execute(this)
			0xEA -> JumpInstruction.Far.Ptr.execute(this)
			0xEB -> JumpInstruction.Short.execute(this)
			0xEE -> OutputToPortInstruction.DXAL.execute(this)
			0xF2, 0xF3 -> {
				var repetitionType = when (opcode) {
					0xF2 -> RepeatType.NOT_ZERO
					0xF3 -> RepeatType.ZERO
					else -> throw IllegalStateException()
				}
				val instruction = when (val stringOpcode = instructionReadU8I()) {
					0xA4 -> MoveDataFromStringToStringInstruction.Bit8
					0xA5 -> MoveDataFromStringToStringInstruction.Bit1632
					0xA6 -> CompareStringOperandsInstruction.Bit8
					0xA7 -> CompareStringOperandsInstruction.Bit1632
					0xAA -> {
						repetitionType = RepeatType.UNCONDITIONAL
						StoreStringDataInstruction.Bit8
					}

					0xAB -> {
						repetitionType = RepeatType.UNCONDITIONAL
						StoreStringDataInstruction.Bit1632
					}

					0xAE -> CompareStringDataInstruction.Bit8
					0xAF -> CompareStringDataInstruction.Bit1632
					else -> TODO(
						"Unknown string opcode for repetition (${stringOpcode.toUByte().toHexString()})"
					)
				}
				this.repeating = RepeatingInstruction(repetitionType, instruction)
				return
			}

			0xF7 -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					4 -> UnsignedMultiplyInstruction.EAXModRm1632(mod, rm)
					5 -> SignedMultiplyInstruction.EAXModRm1632(mod, rm)
					6 -> UnsignedDivideInstruction.EAXModRm1632(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			0xF8 -> ClearCarryFlagInstruction.execute(this)
			0xF9 -> SetCarryFlagInstruction.execute(this)
			0xFA -> ClearInterruptFlagInstruction.execute(this)
			0xFC -> ClearDirectionFlagInstruction.execute(this)
			0xFD -> SetDirectionFlagInstruction.execute(this)
			0xFF -> {
				val (mod, sel, rm) = decompose233(this.instructionReadS8())
				when (sel) {
					2 -> CallProcedureInstruction.NearIndirect(mod, rm)
					3 -> CallProcedureInstruction.GateIndirect(mod, rm)
					else -> TODO("Selector $sel")
				}.execute(this)
			}

			else -> {
				var error = "invalid opcode (${opcode.toUByte().toHexString()}) "
				error += "@ ${CS.base.toHexString()}:${(EIP.q.toUInt() - 1u).toHexString()}"
				TODO(error)
			}
		}
		this.segmentOverride = null
		this.operandSizeOverride = false
		this.addressSizeOverride = false
		this.logger.log(
			InstructionDecodingLogIdentifier(this.CS.base, this.EIP.qu, "TODO: Instruction bytes")
		)
	}
}