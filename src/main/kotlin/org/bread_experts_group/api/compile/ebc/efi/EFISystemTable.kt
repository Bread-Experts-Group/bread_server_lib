package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import org.bread_experts_group.api.compile.ebc.efi.protocol.EFISimpleTextOutputProtocol
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

interface EFISystemTable {
	val header: EFITableHeader
	val firmwareVendor: MemorySegment
	val firmwareRevision: Int
	val consoleInHandle: MemorySegment
	val conIn: Any?
	val consoleOutHandle: MemorySegment
	val conOut: EFISimpleTextOutputProtocol
	val standardErrorHandle: MemorySegment
	val stdErr: EFISimpleTextOutputProtocol
	val runtimeServices: EFIRuntimeServicesTable
	val bootServices: EFIBootServicesTable

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName("org/bread_experts_group/api/compile/ebc/efi/EFISystemTable")
		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getHeader",
					MethodTypeDesc.ofDescriptor("()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;")
				) to { _, _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getFirmwareVendor",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 24u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getFirmwareRevision",
					MethodTypeDesc.ofDescriptor("()I")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSH32(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							1u, 24u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getConOut",
					MethodTypeDesc.ofDescriptor(
						"()Lorg/bread_experts_group/api/compile/ebc/efi/protocol/EFISimpleTextOutputProtocol;"
					)
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							4u, 32u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getBootServices",
					MethodTypeDesc.ofDescriptor(
						"()Lorg/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable;"
					)
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							8u, 32u
						)
					)
				}
			)
	}
}