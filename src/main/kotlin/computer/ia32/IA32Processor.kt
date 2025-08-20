package org.bread_experts_group.computer.ia32

import org.bread_experts_group.computer.BinaryUtil.hex
import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.Processor
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.InstructionCluster
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.InstructionSelector
import org.bread_experts_group.computer.ia32.register.ControlRegister0
import org.bread_experts_group.computer.ia32.register.FlagsRegister
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType
import org.bread_experts_group.computer.ia32.register.Register
import org.bread_experts_group.computer.ia32.register.SegmentRegister
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.logging.Logger
import kotlin.reflect.KProperty0

/**
 * A [Processor] capable of virtualizing the IA-32 architecture.
 * @since 1.0.0
 * @see Computer
 * @author Miko Elbrecht
 */
class IA32Processor : Processor {
	companion object {
		val dummyProcessor = IA32Processor()
	}

	override lateinit var computer: Computer
	override fun step() {
		this.fetch()
		this.decode()
	}

	val decoding: DecodingUtil = DecodingUtil(this)
	val logger: Logger = Logger.getLogger(
		"ia32_processor",
		"org.bread_experts_group.resource.LoggerResource"
	).also {
		it.useParentHandlers = false
	}

	// General Purpose
	val a: Register = Register(this.logger, "a", 0u)
	var b: Register = Register(this.logger, "b", 0u)
	var c: Register = Register(this.logger, "c", 0u)
	var d: Register = Register(this.logger, "d", 0u)
	var sp: Register = Register(this.logger, "sp", 0x6F40u)
	var bp: Register = Register(this.logger, "bp", 0u)

	// Source/Dest
	var di: Register = Register(this.logger, "di", 0u)
	var si: Register = Register(this.logger, "si", 0u)

	// Segment
	var cs: SegmentRegister = SegmentRegister(this, "cs", 0xF000u)
	var ds: SegmentRegister = SegmentRegister(this, "ds", 0u)
	var ss: SegmentRegister = SegmentRegister(this, "ss", 0u)
	var es: SegmentRegister = SegmentRegister(this, "es", 0u)
	var fs: SegmentRegister = SegmentRegister(this, "fs", 0u)
	var gs: SegmentRegister = SegmentRegister(this, "gs", 0u)

	// Global Descriptor Table
	var gdtrLimit: Register = Register(this.logger, "gdtrLimit", 0u)
	var gdtrBase: Register = Register(this.logger, "gdtrBase", 0u)

	// Interrupt Descriptor Table
	var idtrLimit: Register = Register(this.logger, "idtrLimit", 0u)
	var idtrBase: Register = Register(this.logger, "idtrBase", 0u)

	// Control
	val cr0: ControlRegister0 = ControlRegister0(
		this.logger, "cr0",
		ControlRegister0.FlagType.FPU_80387_OR_HIGHER
	)
	val cr2: Register = Register(this.logger, "cr2", 0u)
	val cr3: Register = Register(this.logger, "cr3", 0u)
	val cr4: Register = Register(this.logger, "cr4", 0u)

	// State
	var flags: FlagsRegister = FlagsRegister(this, "flags")

	/**
	 * The current instruction pointer of this [IA32Processor].
	 *
	 * The initial value
	 * (otherwise known as the [Reset Vector](https://en.wikipedia.org/wiki/Reset_vector))
	 * is at physical address `0xFFFFFFF0`, which is the BIOS entry point.
	 * For ease of implementation, the BIOS is not present in ROMs on Bread Mod computers;
	 * instead, a Kotlin-written BIOS will be run, which will then do the boot loading process.
	 * @see fetch
	 * @since 1.0.0
	 * @author Miko Elbrecht
	 */
	var ip: Register = Register(this.logger, "ip", 0xFFF0u)
	var cir: UByte = 0u

	fun push32(value: UInt) {
		when (this.addressSize) {
			AddressingLength.R32 -> this.sp.ex -= 4u
			AddressingLength.R16 -> this.sp.x -= 4u
			else -> throw UnsupportedOperationException()
		}
		this.computer.setMemoryAt32(this.ss.offset(this.sp), value)
	}

	fun push16(value: UShort) {
		when (this.addressSize) {
			AddressingLength.R32 -> this.sp.ex -= 2u
			AddressingLength.R16 -> this.sp.x -= 2u
			else -> throw UnsupportedOperationException()
		}
		this.computer.setMemoryAt16(this.ss.offset(this.sp), value)
	}

	fun pop32(): UInt {
		val popped = this.computer.requestMemoryAt32(this.ss.offset(this.sp))
		when (this.addressSize) {
			AddressingLength.R32 -> this.sp.ex += 4u
			AddressingLength.R16 -> this.sp.x += 4u
			else -> throw UnsupportedOperationException()
		}
		return popped
	}

	fun pop16(): UShort {
		val popped = this.computer.requestMemoryAt16(this.ss.offset(this.sp))
		when (this.addressSize) {
			AddressingLength.R32 -> this.sp.ex += 2u
			AddressingLength.R16 -> this.sp.x += 2u
			else -> throw UnsupportedOperationException()
		}
		return popped
	}

	override fun reset() {
		this.a.rx = 0u
		this.b.rx = 0u
		this.c.rx = 0u
		this.d.rx = 0u
		this.sp.rx = 0x6F40u
		this.bp.rx = 0u
		this.di.rx = 0u
		this.si.rx = 0u
		this.cs.rx = 0xF000u
		this.gdtrLimit.rx = 0u
		this.gdtrBase.rx = 0u
		this.idtrLimit.rx = 0u
		this.idtrBase.rx = 0u
		this.ip.rx = 0xFFF0u
		this.ds.rx = 0u
		this.ss.rx = 0u
		this.es.rx = 0u
		this.fs.rx = 0u
		this.gs.rx = 0u
		this.cr0.rx = 0u
		this.cr2.rx = 0u
		this.cr3.rx = 0u
		this.cr4.rx = 0u
		this.flags.rx = 0u
		this.cir = 0u
		this.halt.countDown()
	}

	val biosHooks: MutableMap<ULong, MutableMap<ULong, (IA32Processor) -> Unit>> = mutableMapOf()
	fun setHook(cs: ULong, ip: ULong, r: (IA32Processor) -> Unit) {
		this.biosHooks.getOrPut(cs) { mutableMapOf() }[ip] = r
	}

	var halt: CountDownLatch = CountDownLatch(1)

	fun initiateInterrupt(selector: UByte) {
		this.halt.countDown()
		if (this.realMode()) {
			this.push16(this.flags.tx)
			this.flags.setFlag(FlagType.INTERRUPT_ENABLE_FLAG, false)
			this.flags.setFlag(FlagType.TRAP_FLAG, false)
			this.flags.setFlag(FlagType.AUXILIARY_CARRY_FLAG, false)
			this.logger.warning("!!! INTERRUPT RECEIVED (${hex(selector)}) !!!")
			this.push16(this.cs.tx)
			this.push16(this.ip.tx)
			val addr = selector.toULong() * 4u
			this.ip.tex = this.computer.requestMemoryAt16(addr).toUInt()
			this.cs.tx = this.computer.requestMemoryAt16(addr + 2u)
		} else {
			TODO("Protected mode interrupts")
		}
	}

	fun fetch() {
		this.halt.await()
		if (this.realMode()) this.biosHooks[this.cs.rx]?.get(this.ip.rx)?.invoke(this)
		this.cir = this.computer.requestMemoryAt(this.cs.offset(this.ip))
		this.ip.rx++
	}

	val instructionMap: MutableMap<UInt, Instruction> = mutableMapOf()
	var segment: SegmentRegister? = null
	private var operandSizeOverride: Boolean = false
	private var addressSizeOverride: Boolean = false
	private var memoryLock: Boolean = false // TODO, No-op
	fun realMode(): Boolean = !this.cr0.getFlag(ControlRegister0.FlagType.PROTECTED_MODE_ENABLE)

	fun getAddressingLengthForSpecifier(specifier: KProperty0<Boolean>): AddressingLength {
		if (!this.realMode()) {
			// Protected Mode
			if (this.cs.readSegmentDescriptor().flags and 0b0100u > 0u) {
				return if (specifier.get()) AddressingLength.R16
				else AddressingLength.R32
			}
			return if (specifier.get()) AddressingLength.R32
			else AddressingLength.R16
		}
		// Real Mode
		return if (specifier.get()) AddressingLength.R32
		else AddressingLength.R16
	}

	val operandSize: AddressingLength
		get() = this.getAddressingLengthForSpecifier(this::operandSizeOverride)
	val addressSize: AddressingLength
		get() = this.getAddressingLengthForSpecifier(this::addressSizeOverride)

	init {
		ServiceLoader.load(Instruction::class.java).forEach {
			require(!this.instructionMap.contains(it.opcode)) { "Multiple opcodes, ${hex(it.opcode)}" }
			this.instructionMap[it.opcode] = it
		}
		ServiceLoader.load(InstructionSelector::class.java).forEach {
			it.initialize(this)
			require(!this.instructionMap.contains(it.opcode)) { "Multiple opcodes, ${hex(it.opcode)}" }
			this.instructionMap[it.opcode] = it
		}
		ServiceLoader.load(InstructionCluster::class.java).forEach {
			it.getInstructions(this).forEach { f ->
				require(!this.instructionMap.contains(f.opcode)) { "Multiple opcodes, ${hex(f.opcode)}" }
				this.instructionMap[f.opcode] = f
			}
		}
	}

	private var readingOffPrefix: UInt = 0u
	private var localExecutor: ((Instruction) -> Unit)? = null
	fun decode() {
		val instruction = when (this.cir.toUInt()) {
			0x26u -> {
				this.segment = this.es
				return
			}

			0x2Eu -> {
				this.segment = this.cs
				return
			}

			0x36u -> {
				this.segment = this.ss
				return
			}

			0x3Eu -> {
				this.segment = this.ds
				return
			}

			0x64u -> {
				this.segment = this.fs
				return
			}

			0x65u -> {
				this.segment = this.gs
				return
			}

			0x66u -> {
				this.operandSizeOverride = true
				return
			}

			0x67u -> {
				this.addressSizeOverride = true
				return
			}

			0x0Fu -> this.instructionMap[(0x0Fu shl 8) or this.decoding.readFetch().toUInt()]
				?: throw IllegalArgumentException(
					"Missing two-byte opcode (0F) for ${hex(this.cir)} [${hex(this.ip.rx)}]"
				)

			0xF0u -> {
				this.memoryLock = true
				return
			}

			0xF2u -> {
				this.localExecutor = {
					val c = when (this.addressSize) {
						AddressingLength.R32 -> this.c::ex
						AddressingLength.R16 -> this.c::x
						else -> throw UnsupportedOperationException()
					}
					while (c.get() > 0u) {
						it.handle(this)
						c.set(c.get() - 1u)
						if (
							(it.opcode == 0xA6u || it.opcode == 0xA7u ||
									it.opcode == 0xAEu || it.opcode == 0xAFu) &&
							this.flags.getFlag(FlagType.ZERO_FLAG)
						) break
					}
				}
				return
			}

			0xF3u -> {
				this.localExecutor = {
					val c = when (this.addressSize) {
						AddressingLength.R32 -> this.c::ex
						AddressingLength.R16 -> this.c::x
						else -> throw UnsupportedOperationException()
					}
					this.flags.setFlag(FlagType.ZERO_FLAG, true)
					while (c.get() > 0u) {
						it.handle(this)
						c.set(c.get() - 1u)
						if (
							(it.opcode == 0xA6u || it.opcode == 0xA7u ||
									it.opcode == 0xAEu || it.opcode == 0xAFu) &&
							!this.flags.getFlag(FlagType.ZERO_FLAG)
						) break
					}
				}
				return
			}

			else -> {
				if (this.readingOffPrefix > 0u) {
					(this.instructionMap[(this.readingOffPrefix shl 8) or this.cir.toUInt()]
						?: throw IllegalArgumentException(
							"Missing two-byte opcode (${hex(this.readingOffPrefix)}) for " +
									"${hex(this.cir)} [${hex(this.ip.rx)}]"
						)).also { this.readingOffPrefix = 0u }
				} else {
					this.instructionMap[this.cir.toUInt()] ?: throw IllegalArgumentException(
						"Missing opcode for ${hex(this.cir)} [${this.cs.hex(this.ip.rx - 1u)}]"
					)
				}
			}
		}
		this.logger.warning { "${this.cs.hex(this.ip.rx - 1u)} ${hex(this.cir)}: ${instruction.getDisassembly(this)}" }
		localExecutor?.invoke(instruction) ?: instruction.handle(this)
		this.localExecutor = null
		this.segment = null
		this.operandSizeOverride = false
		this.addressSizeOverride = false
		this.memoryLock = false
	}
}