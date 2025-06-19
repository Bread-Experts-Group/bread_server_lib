package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.assembler.Assembler
import org.bread_experts_group.computer.ia32.assembler.AssemblerMemRM.Companion.asmMemRM
import org.bread_experts_group.computer.ia32.assembler.AssemblerRegister.Companion.asmRegister
import org.bread_experts_group.computer.ia32.assembler.modRmByte
import org.bread_experts_group.computer.ia32.instruction.AssembledInstruction
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.AddressingLength
import org.bread_experts_group.computer.ia32.instruction.DecodingUtil.RegisterType
import org.bread_experts_group.computer.ia32.instruction.type.Instruction
import org.bread_experts_group.computer.ia32.instruction.type.flag.LogicalArithmeticFlagOperations
import org.bread_experts_group.computer.ia32.instruction.type.operand.ModRM
import java.io.OutputStream
import java.util.logging.Logger

/**
 * Opcode: `31 /r` |
 * Instruction: `XOR r/m(16/32), r(16/32)` |
 * Flags Modified: `OF, CR` (clr) / `SF, ZF, PF` (result dep)
 * @author Miko Elbrecht
 * @since 1.0.0
 */
class ModRMRegisterXOR : Instruction(0x31u, "xor"), ModRM, LogicalArithmeticFlagOperations, AssembledInstruction {
	override fun operands(processor: IA32Processor): String = processor.rmD().let { "${it.regMem}, ${it.register}" }
	override fun handle(processor: IA32Processor) {
		val (memRM, register) = processor.rm()
		when (processor.operandSize) {
			AddressingLength.R32 -> {
				val result = memRM.getRMi() xor register.get().toUInt()
				memRM.setRMi(result)
				this.setFlagsForResult(processor, result)
			}

			AddressingLength.R16 -> {
				val result = memRM.getRMs() xor register.get().toUShort()
				memRM.setRMs(result)
				this.setFlagsForResult(processor, result)
			}

			else -> throw UnsupportedOperationException()
		}
	}

	override val registerType: RegisterType = RegisterType.GENERAL_PURPOSE
	override val arguments: Int = 2
	override fun acceptable(logger: Logger, from: ArrayDeque<String>): Boolean {
		val memRM = from[0].asmMemRM(logger)
		val register = from[1].asmRegister(logger)
		return memRM != null && register != null
	}

	override fun produce(logger: Logger, mode: Assembler.BitMode, into: OutputStream, from: ArrayDeque<String>) {
		if (mode != Assembler.BitMode.BITS_16) TODO(mode.name)
		into.write(0x31)
		val memRM = from.removeFirst().asmMemRM(logger)!!
		val register = from.removeFirst().asmRegister(logger)!!
		into.write(modRmByte(memRM, register))
	}
}