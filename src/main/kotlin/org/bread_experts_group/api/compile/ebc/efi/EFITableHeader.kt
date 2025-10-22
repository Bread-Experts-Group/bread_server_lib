package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

interface EFITableHeader {
	val segment: MemorySegment
	val signature: Long
	val revision: Int
	val headerSize: Int
	val crc32: Int
	val reserved: Int

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName("org/bread_experts_group/api/compile/ebc/efi/EFITableHeader")
		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSegment",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSignature",
					MethodTypeDesc.ofDescriptor("()J")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 0u
						)
					)
				},
			)
	}
}