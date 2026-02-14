package org.bread_experts_group.api.compile.ebc.efi

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex16
import org.bread_experts_group.api.compile.ebc.EBCProcedure.Companion.naturalIndex32
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.intrinsic.KotlinEBCIntrinsicProvider
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc
import java.lang.foreign.MemorySegment

interface EFIBootServicesTable {
	val header: EFITableHeader
	fun allocatePool(poolType: EFIMemoryType, size: Long): EFIStatusReturned1
	fun locateProtocol(protocol: MemorySegment, registration: MemorySegment): EFIStatusReturned1

	class IntrinsicProvider : KotlinEBCIntrinsicProvider {
		private val owner = ClassDesc.ofInternalName("org/bread_experts_group/api/compile/ebc/efi/EFIBootServicesTable")
		private val ret1 = "Lorg/bread_experts_group/api/compile/ebc/efi/EFIStatusReturned1;"

		companion object {
			val allocatePool: (EBCProcedure, EBCCompilerData) -> Unit = { procedure, data ->
				procedure.POP64(EBCRegisters.R6, false, null) // Size
				procedure.POP32(EBCRegisters.R5, false, null) // PoolType
				procedure.POPn(EBCRegisters.R4, false, null) // BootServices
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
				procedure.PUSH64(EBCRegisters.R3, false, null) // **Buffer
				procedure.PUSHn(EBCRegisters.R6, false, null) // Size
				procedure.PUSHn(EBCRegisters.R5, false, null) // PoolType
				procedure.CALL32(
					EBCRegisters.R4,
					operand1Indirect = true,
					relative = false,
					native = true,
					immediate = naturalIndex32(
						false,
						5u, 24u
					)
				)
				procedure.MOVnw(
					EBCRegisters.R0, false,
					EBCRegisters.R0, false,
					null, naturalIndex16(
						false,
						2u, 8u
					)
				)
				// TODO: Class returns
				procedure.PUSH64(EBCRegisters.R7, false, null)
				procedure.PUSHn(EBCRegisters.R3, true, null)
			}
		}

		override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCCompilerData) -> Unit> = mapOf(
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"getHeader",
				MethodTypeDesc.ofDescriptor("()Lorg/bread_experts_group/api/compile/ebc/efi/EFITableHeader;")
			) to { _, _ -> },
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"allocatePool",
				MethodTypeDesc.ofDescriptor("(Lorg/bread_experts_group/api/compile/ebc/efi/EFIMemoryType;J)$ret1")
			) to allocatePool,
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"locateProtocol",
				MethodTypeDesc.ofDescriptor(
					"(Ljava/lang/foreign/MemorySegment;Ljava/lang/foreign/MemorySegment;)$ret1"
				)
			) to { procedure, data ->
				procedure.POPn(EBCRegisters.R5, false, null) // Registration
				procedure.POPn(EBCRegisters.R6, false, null) // Protocol
				procedure.POPn(EBCRegisters.R4, false, null) // BootServices
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
				procedure.PUSHn(EBCRegisters.R3, false, null) // **Interface
				procedure.PUSHn(EBCRegisters.R5, false, null) // Registration
				procedure.PUSHn(EBCRegisters.R6, false, null) // Protocol
				procedure.CALL32(
					EBCRegisters.R4,
					operand1Indirect = true,
					relative = false,
					native = true,
					immediate = naturalIndex32(
						false,
						37u, 24u
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
				// TODO Class returns
				procedure.PUSH64(EBCRegisters.R7, false, null)
				procedure.PUSHn(EBCRegisters.R3, true, null)
			},
		)
	}
}