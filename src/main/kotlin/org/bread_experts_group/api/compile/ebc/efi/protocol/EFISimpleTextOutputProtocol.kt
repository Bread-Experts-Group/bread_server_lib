package org.bread_experts_group.api.compile.ebc.efi.protocol

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex32
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

interface EFISimpleTextOutputProtocol {
	val segment: MemorySegment
	fun reset(extendedVerification: Boolean): Long
	fun outputString(string: String): Long
	fun outputStringAt(address: MemorySegment): Long

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName(
			"org/bread_experts_group/api/compile/ebc/efi/protocol/EFISimpleTextOutputProtocol"
		)

		private val outputStringShared = { procedure: EBCProcedure, stack: EBCStackTracker, _: EBCCompilerData ->
			stack.POPn(EBCRegisters.R6, false, null)
			stack.POPn(EBCRegisters.R5, false, null)
			procedure.PUSHn(EBCRegisters.R6, false, null)
			procedure.PUSHn(EBCRegisters.R5, false, null)
			procedure.CALL32(
				EBCRegisters.R5,
				operand1Indirect = true,
				relative = false,
				native = true,
				immediate = naturalIndex32(
					false,
					1u, 0u
				)
			)
			procedure.MOVnw(
				EBCRegisters.R0, false,
				EBCRegisters.R0, false,
				null, naturalIndex16(
					false,
					2u, 0u
				)
			)
			stack.PUSH64(EBCRegisters.R7, false, null)
		}

		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSegment",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"reset",
					MethodTypeDesc.ofDescriptor("(Z)J")
				) to { procedure, stack, _ ->
					stack.POP32(EBCRegisters.R6, false, null)
					stack.POPn(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					procedure.PUSHn(EBCRegisters.R5, false, null)
					procedure.CALL32(
						EBCRegisters.R5,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = null
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							2u, 0u
						)
					)
					stack.PUSH64(EBCRegisters.R7, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"outputString",
					MethodTypeDesc.ofDescriptor("(Ljava/lang/String;)J")
				) to outputStringShared,
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"outputStringAt",
					MethodTypeDesc.ofDescriptor("(Ljava/lang/foreign/MemorySegment;)J")
				) to outputStringShared,
			)
	}
}