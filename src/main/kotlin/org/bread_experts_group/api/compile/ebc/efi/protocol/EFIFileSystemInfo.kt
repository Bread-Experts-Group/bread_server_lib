package org.bread_experts_group.api.compile.ebc.efi.protocol

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

interface EFIFileSystemInfo {
	val segment: MemorySegment
	val structureSize: Long
	val readOnly: Boolean
	val volumeSize: Long
	val freeSpace: Long
	val blockSize: Int
	val volumeLabel: MemorySegment

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName(
			"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileSystemInfo"
		)

		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSegment",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getStructureSize",
					MethodTypeDesc.ofDescriptor("()J")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 0u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getReadOnly",
					MethodTypeDesc.ofDescriptor("()Z")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH32(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 8u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getVolumeSize",
					MethodTypeDesc.ofDescriptor("()J")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 16u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getFreeSpace",
					MethodTypeDesc.ofDescriptor("()J")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 24u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getBlockSize",
					MethodTypeDesc.ofDescriptor("()I")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSH32(
						EBCRegisters.R6, true,
						naturalIndex16(
							false,
							0u, 32u
						)
					)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getVolumeLabel",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R6, false, null)
					procedure.PUSHn(
						EBCRegisters.R6, false,
						naturalIndex16(
							false,
							0u, 36u
						)
					)
				},
			)
	}
}