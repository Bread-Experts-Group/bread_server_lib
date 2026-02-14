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

interface EFITime { // 16 bytes long
	val segment: MemorySegment
	val year: Short
	val month: Byte
	val day: Byte
	val hour: Byte
	val minute: Byte
	val second: Byte
	val nanosecond: Long
	val timeZone: Short
	val daylight: Byte

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.of("org.bread_experts_group.api.compile.ebc.efi.protocol.EFITime")
		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCCompilerData) -> Unit> =
			mapOf(
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSegment",
					MethodTypeDesc.ofDescriptor("()Ljava/lang/foreign/MemorySegment;")
				) to { _, _ -> },
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getYear",
					MethodTypeDesc.ofDescriptor("()S")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVww(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true, null
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getMonth",
					MethodTypeDesc.ofDescriptor("()B")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVbw(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 2u)
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getDay",
					MethodTypeDesc.ofDescriptor("()B")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVbw(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 3u)
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getHour",
					MethodTypeDesc.ofDescriptor("()B")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVbw(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 4u)
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getMinute",
					MethodTypeDesc.ofDescriptor("()B")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVbw(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 5u)
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getSecond",
					MethodTypeDesc.ofDescriptor("()B")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVbw(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 6u)
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getNanosecond",
					MethodTypeDesc.ofDescriptor("()J")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIqw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVdw(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 8u)
					)
					procedure.PUSH64(EBCRegisters.R6, false, null)
				},
				MethodHandleDesc.ofMethod(
					DirectMethodHandleDesc.Kind.SPECIAL, owner,
					"getTimeZone",
					MethodTypeDesc.ofDescriptor("()S")
				) to { procedure, _ ->
					procedure.POPn(EBCRegisters.R5, false, null)
					procedure.MOVIdw(EBCRegisters.R6, false, null, 0u)
					procedure.MOVww(
						EBCRegisters.R6, false, null,
						EBCRegisters.R5, true,
						naturalIndex16(false, 0u, 12u)
					)
					procedure.PUSH32(EBCRegisters.R6, false, null)
				},
			)
	}
}