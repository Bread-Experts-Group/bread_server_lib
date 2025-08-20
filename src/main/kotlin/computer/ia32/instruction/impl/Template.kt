package org.bread_experts_group.computer.ia32.instruction.impl

import org.bread_experts_group.computer.ia32.IA32Processor
import org.bread_experts_group.computer.ia32.register.FlagsRegister.FlagType

fun carry10i(processor: IA32Processor): UInt = if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u
fun carry10s(processor: IA32Processor): UShort = if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u
fun carry10b(processor: IA32Processor): UByte = if (processor.flags.getFlag(FlagType.CARRY_FLAG)) 1u else 0u