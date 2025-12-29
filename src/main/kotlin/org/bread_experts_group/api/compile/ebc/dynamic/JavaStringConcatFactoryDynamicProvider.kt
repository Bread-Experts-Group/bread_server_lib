package org.bread_experts_group.api.compile.ebc.dynamic

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import org.bread_experts_group.api.compile.ebc.efi.EFIBootServicesTable.IntrinsicProvider.Companion.allocatePool
import org.bread_experts_group.api.compile.ebc.efi.EFISystemTable.IntrinsicProvider.Companion.getBootServices
import java.lang.constant.*

class JavaStringConcatFactoryDynamicProvider : KotlinEBCDynamicProvider {
	private val owner = ClassDesc.of("java.lang.invoke.StringConcatFactory")

	override fun dynamics(
	): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData, List<ConstantDesc>) -> Unit> = mapOf(
		MethodHandleDesc.ofMethod(
			DirectMethodHandleDesc.Kind.SPECIAL, owner,
			"makeConcatWithConstants",
			MethodTypeDesc.ofDescriptor("(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
		) to { procedure, stack, data, arguments ->
			@Suppress("CAST_NEVER_SUCCEEDS") val template = arguments[0] as String
			stack.POPn(EBCRegisters.R7, false, null) // T1
			stack.POPn(EBCRegisters.R6, false, null) // T0
			stack.PUSHn(EBCRegisters.R6, false, null)
			stack.PUSHn(EBCRegisters.R7, false, null)
			procedure.MOVIqw(
				EBCRegisters.R5, false, null,
				template.filter { it.code != 0 && it.code != 1 }.length.toUShort()
			)
			procedure.MOVIdw(EBCRegisters.R4, false, null, 0u)
			// T0 Count
			procedure.MOVww( // 1
				EBCRegisters.R4, false, null,
				EBCRegisters.R6, true, null
			)
			procedure.CMPI32weq( // 2
				EBCRegisters.R4, false, null,
				0u
			)
			procedure.JMP8( // 1
				conditional = true, conditionSet = true,
				wordOffset = 6
			)
			procedure.MOVIdw( // 2
				EBCRegisters.R4, false, null,
				2u
			)
			procedure.ADD32( // 1
				EBCRegisters.R6, false,
				EBCRegisters.R4, false, null
			)
			procedure.ADD32( // 2
				EBCRegisters.R5, false,
				EBCRegisters.R4, false, (-1).toUShort()
			)
			procedure.JMP8( // 1
				conditional = false, conditionSet = false,
				wordOffset = -10
			)
			// End T0 Count
			// TODO: PROCEDURIZE
			// T1 Count
			procedure.MOVww( // 1
				EBCRegisters.R4, false, null,
				EBCRegisters.R7, true, null
			)
			procedure.CMPI32weq( // 2
				EBCRegisters.R4, false, null,
				0u
			)
			procedure.JMP8( // 1
				conditional = true, conditionSet = true,
				wordOffset = 6
			)
			procedure.MOVIdw( // 2
				EBCRegisters.R4, false, null,
				2u
			)
			procedure.ADD32( // 1
				EBCRegisters.R7, false,
				EBCRegisters.R4, false, null
			)
			procedure.ADD32( // 2
				EBCRegisters.R5, false,
				EBCRegisters.R4, false, (-1).toUShort()
			)
			procedure.JMP8( // 1
				conditional = false, conditionSet = false,
				wordOffset = -10
			)
			// End T1 Count
			// TODO: AUTO REGISTER ALLOCATION
			// R4 is empty here
			procedure.ADD32(
				EBCRegisters.R5, false,
				EBCRegisters.R4, false, 1u
			)
			procedure.MUL32(
				EBCRegisters.R5, false,
				EBCRegisters.R4, false, 2u
			)
			// String allocation
			procedure.MOVIqq(
				EBCRegisters.R1, false, null,
				data.unInitBase
			)
			procedure.PUSHn(
				EBCRegisters.R1, true, naturalIndex16(
					false,
					data.systemTableNatural, data.systemTableConstant
				)
			)
			getBootServices(procedure, stack, data)
			stack.PUSH32(
				EBCRegisters.R4, false,
				2u // EfiLoaderData
			)
			stack.PUSH64(EBCRegisters.R5, false, null)
			allocatePool(procedure, stack, data)
			procedure.POPn(EBCRegisters.R5, false, null)
			procedure.POP64(EBCRegisters.R6, false, null) // TODO: error handling
			val templatePosition = data.allocator.getOrAllocateString(template)
			// T1 Concatenate
			// TODO: Concatenate
			// T1 Concatenate end
			// T0 Concatenate
			// TODO: Concatenate
			// T0 Concatenate end
			procedure.POPn(EBCRegisters.R2, false, null)
			procedure.POPn(EBCRegisters.R2, false, null)
			stack.PUSHn(EBCRegisters.R5, false, null)
		}
	)
}