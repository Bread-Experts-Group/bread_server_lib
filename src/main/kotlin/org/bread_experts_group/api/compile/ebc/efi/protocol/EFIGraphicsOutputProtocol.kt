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

interface EFIGraphicsOutputProtocol {
	val segment: MemorySegment
	fun blt(
		bltBuffer: MemorySegment,
		bltOperation: Int,
		sourceX: Long,
		sourceY: Long,
		destinationX: Long,
		destinationY: Long,
		width: Long,
		height: Long,
		delta: Long
	): Long

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		val owner = ClassDesc.ofInternalName(
			"org/bread_experts_group/api/compile/ebc/efi/protocol/EFIGraphicsOutputProtocol"
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
					"blt",
					MethodTypeDesc.ofDescriptor("(Ljava/lang/foreign/MemorySegment;IJJJJJJJ)J")
				) to { procedure, stack, data ->
					procedure.MOVIqq(
						EBCRegisters.R6, false, null,
						data.instructionSpaceBase
					)
					stack.POP64(
						EBCRegisters.R6, true,
						null
					) // Delta
					stack.POP64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 8u)
					) // Height
					stack.POP64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 16u)
					) // Width
					stack.POP64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 24u)
					) // DestinationY
					stack.POP64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 32u)
					) // DestinationX
					stack.POP64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 40u)
					) // SourceY
					stack.POP64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 48u)
					) // SourceX
					stack.POP32(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 56u)
					) // BltOperation
					stack.POPn(
						EBCRegisters.R6, true,
						naturalIndex16(false, 1u, 56u)
					) // *BltBuffer
					stack.POPn(EBCRegisters.R4, false, null) // *This
					procedure.PUSH64(
						EBCRegisters.R6, true,
						null
					) // Delta
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 8u)
					) // Height
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 16u)
					) // Width
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 24u)
					) // DestinationY
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 32u)
					) // DestinationX
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 40u)
					) // SourceY
					procedure.PUSH64(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 48u)
					) // SourceX
					procedure.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(false, 0u, 56u)
					) // BltOperation
					procedure.PUSHn(
						EBCRegisters.R6, true,
						naturalIndex16(false, 1u, 56u)
					) // *BltBuffer
					procedure.PUSHn(EBCRegisters.R4, false, null) // *This
					procedure.CALL32(
						EBCRegisters.R4,
						operand1Indirect = true,
						relative = false,
						native = true,
						immediate = naturalIndex32(
							false,
							2u, 0u
						)
					)
					procedure.MOVnw(
						EBCRegisters.R0, false,
						EBCRegisters.R0, false,
						null, naturalIndex16(
							false,
							3u, 56u
						)
					)
					stack.PUSH64(EBCRegisters.R7, false, null)
				},
			)
	}
}