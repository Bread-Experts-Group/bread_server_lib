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
			stack.POPn(EBCRegisters.R4, false, null) // S2
			stack.POPn(EBCRegisters.R3, false, null) // S1
			procedure.MOVIqq(
				EBCRegisters.R1, false, null,
				data.unInitBase
			)
			procedure.PUSHn(
				EBCRegisters.R6, true, naturalIndex16(
					false,
					data.systemTableNatural, data.systemTableConstant
				)
			)
			getBootServices(procedure, stack, data)
			procedure.MOVIdw(EBCRegisters.R6, false, null, 2u) // TODO: Correct pool
			procedure.PUSH32(EBCRegisters.R6, false, null)
			@Suppress("CAST_NEVER_SUCCEEDS")
			val template = arguments[0] as String
			val replaceCount = template.sumOf { if (it.code == 1 || it.code == 2) 1 else 0 }
			procedure.MOVIdd(
				EBCRegisters.R6, false, null,
				((template.length - replaceCount) + 1).toUInt() * 2u
			)

			val (countNatural, countConstant) = data.allocator.bump32()
			procedure.MOVIqw(
				EBCRegisters.R5, false, null,
				2u
			)
			procedure.ADD64(
				EBCRegisters.R4, false,
				EBCRegisters.R5, false, null
			)
			procedure.MOVww(
				EBCRegisters.R5, false, null,
				EBCRegisters.R4, true, naturalIndex16(true, 0u, 2u)
			)
			procedure.MOVIqw(
				EBCRegisters.R2, false, null,
				0u
			)
			procedure.CMP64eq(
				EBCRegisters.R5,
				EBCRegisters.R2, false, null
			)
			procedure.JMP8(
				true,
				conditionSet = true,
				wordOffset = 8
			)
			procedure.MOVIqw(
				EBCRegisters.R2, false, null,
				1u
			)
			procedure.MOVdw(
				EBCRegisters.R5, false, null,
				EBCRegisters.R1, true, naturalIndex16(
					false,
					countNatural, countConstant
				)
			)
			procedure.ADD32(
				EBCRegisters.R5, false,
				EBCRegisters.R2, false, null
			)
			procedure.MOVdw(
				EBCRegisters.R1, true, naturalIndex16(
					false,
					countNatural, countConstant
				),
				EBCRegisters.R5, false, null
			)
			procedure.JMP8(
				conditional = false,
				conditionSet = false,
				wordOffset = -17
			)

			procedure.PUSH64(EBCRegisters.R6, false, null)
			allocatePool(procedure, stack, data)
			procedure.POPn(EBCRegisters.R6, false, null) // Concatenated
			procedure.POP64(EBCRegisters.R5, false, null) // TODO: Error handling

			// CONCATENATE HERE !!!!!!!!!!

			procedure.PUSHn(EBCRegisters.R6, false, null)
			procedure.POPn(EBCRegisters.R6, false, null) // TODO gunk
			procedure.POPn(EBCRegisters.R6, false, null) // TODO gunk
			procedure.MOVqw(
				EBCRegisters.R7, false, null,
				EBCRegisters.R1, true, naturalIndex16(
					false,
					countNatural, countConstant
				)
			)
			procedure.RET()
		}
	)
}