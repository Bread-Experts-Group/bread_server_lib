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

interface EFIFileProtocol {
	val segment: MemorySegment
	val revision: Long
	fun open(fileName: String, openMode: Long, attributes: Long): EFIStatusReturned1
	fun read(bufferSize: MemorySegment, buffer: MemorySegment): Long
	fun getInfo(informationTypeGUID: MemorySegment, bufferSize: MemorySegment, buffer: MemorySegment): Long

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName(
			"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIFileProtocol"
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
					"open",
					MethodTypeDesc.ofDescriptor(
						"(Ljava/lang/String;JJ)Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;"
					)
				) to { procedure, stack, data ->
					stack.POP64(EBCRegisters.R7, false, null) // Attributes
					stack.POP64(EBCRegisters.R6, false, null) // OpenMode
					stack.POPn(EBCRegisters.R5, false, null) // *FileName
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
					procedure.PUSH64(EBCRegisters.R7, false, null) // Attributes
					procedure.PUSH64(EBCRegisters.R6, false, null) // OpenMode
					procedure.PUSHn(EBCRegisters.R5, false, null) // *FileName
					procedure.PUSHn(EBCRegisters.R3, false, null) // **NewHandle
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
							5u, 0u
						)
					)
					stack.PUSH64(EBCRegisters.R7, false, null)
					stack.PUSHn(EBCRegisters.R3, true, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"read",
					MethodTypeDesc.ofDescriptor("(Ljava/lang/foreign/MemorySegment;Ljava/lang/foreign/MemorySegment;)J")
				) to { procedure, stack, _ ->
					stack.POPn(EBCRegisters.R7, false, null) // *Buffer
					stack.POPn(EBCRegisters.R6, false, null) // *BufferSize
					stack.POPn(EBCRegisters.R4, false, null) // *This
					procedure.PUSHn(EBCRegisters.R7, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					procedure.PUSHn(EBCRegisters.R4, false, null)
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							3u, 8u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							3u, 0u
						)
					)
					stack.PUSH64(EBCRegisters.R7, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getInfo",
					MethodTypeDesc.ofDescriptor(
						"(Ljava/lang/foreign/MemorySegment;Ljava/lang/foreign/MemorySegment" +
								";Ljava/lang/foreign/MemorySegment;)J"
					)
				) to { procedure, stack, _ ->
					stack.POPn(EBCRegisters.R7, false, null) // *Buffer
					stack.POPn(EBCRegisters.R6, false, null) // *BufferSize
					stack.POPn(EBCRegisters.R5, false, null) // *InformationType
					stack.POPn(EBCRegisters.R4, false, null) // *This
					procedure.PUSHn(EBCRegisters.R7, false, null)
					procedure.PUSHn(EBCRegisters.R6, false, null)
					procedure.PUSHn(EBCRegisters.R5, false, null)
					procedure.PUSHn(EBCRegisters.R4, false, null)
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							7u, 8u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							4u, 0u
						)
					)
					stack.PUSH64(EBCRegisters.R7, false, null)
				},
			)
	}
}