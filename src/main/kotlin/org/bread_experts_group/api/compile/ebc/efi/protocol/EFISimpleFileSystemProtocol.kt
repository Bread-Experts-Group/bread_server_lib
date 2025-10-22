package org.bread_experts_group.api.compile.ebc.efi.protocol

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex32
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import org.bread_experts_group.api.compile.ebc.efi.EFIStatusReturned1
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

interface EFISimpleFileSystemProtocol {
	val segment: MemorySegment
	val revision: Long
	fun openVolume(): EFIStatusReturned1

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName(
			"org/bread_experts_group/api/compile/ebc/efi/protocol/EFISimpleFileSystemProtocol"
		)

		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSegment",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getRevision",
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
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"openVolume",
					MethodTypeDesc.ofDescriptor("()Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;")
				) to { procedure, stack, data ->
					stack.POPn(EBCRegisters.R4, false, null) // *This
					procedure.MOVIqq(
						EBCRegisters.R3, false, null,
						data.unInitBase
					)
					procedure.MOVqw(
						EBCRegisters.R3, false, null,
						EBCRegisters.R3, false, naturalIndex16(
							false,
							data.allocatorNatural, data.allocatorConstant
						)
					)
					procedure.PUSHn(EBCRegisters.R3, false, null) // **Root
					procedure.PUSHn(EBCRegisters.R4, false, null) // *This
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							0u, 8u
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
					stack.PUSHn(EBCRegisters.R3, true, null)
				},
			)
	}
}