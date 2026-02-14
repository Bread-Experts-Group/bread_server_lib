package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

data class EFIStatusReturned1(
	val status: Long,
	val data: MemorySegment
) {
	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName("org/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1")
		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getData",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.POP64(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getStatus",
					MethodTypeDesc.ofDescriptor("()J")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
				},
			)
	}
}
