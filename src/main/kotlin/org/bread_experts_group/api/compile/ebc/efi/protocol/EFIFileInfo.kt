package org.bread_experts_group.api.compile.ebc.efi.protocol

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

interface EFIFileInfo {
	val segment: MemorySegment
	val structureSize: Long
	val fileSize: Long
	val physicalSize: Long
	val creationTime: EFITime
	val lastAccessTime: EFITime
	val modificationTime: EFITime
	val attribute: Long
	val fileName: MemorySegment

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName("org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileInfo")
		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSegment",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getFileSize",
					MethodTypeDesc.ofDescriptor("()J")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 8u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getPhysicalSize",
					MethodTypeDesc.ofDescriptor("()J")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 16u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getCreationTime",
					MethodTypeDesc.ofDescriptor("()Lorg/bread_experts_group/api/compile/ebc/efi/protocol/EFITime;")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, false,
						naturalIndex16(
							false,
							0u, 24u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getLastAccessTime",
					MethodTypeDesc.ofDescriptor("()Lorg/bread_experts_group/api/compile/ebc/efi/protocol/EFITime;")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, false,
						naturalIndex16(
							false,
							0u, 40u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getModificationTime",
					MethodTypeDesc.ofDescriptor("()Lorg/bread_experts_group/api/compile/ebc/efi/protocol/EFITime;")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, false,
						naturalIndex16(
							false,
							0u, 56u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getAttribute",
					MethodTypeDesc.ofDescriptor("()J")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 72u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getFileName",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, stack, _ ->
					stack.POPn(EBCRegisters.R6, false, null)
					stack.PUSHn(
						EBCRegisters.R6, false,
						naturalIndex16(
							false,
							0u, 80u
						)
					)
				},
			)
	}
}