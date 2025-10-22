package org.bread_experts_group.api.compile.ebc.intrinsic

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc

class JavaForeignMemorySegmentIntrinsicProvider : KotlinEBCIntrinsicProvider {
	private val owner = ClassDesc.ofInternalName("java/lang/foreign/MemorySegment")
	override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
		mapOf(
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"asSlice",
				MethodTypeDesc.ofDescriptor("(J)Ljava/lang/foreign/MemorySegment;")
			) to { procedure, stack, _ ->
				stack.POP64(EBCRegisters.R6, false, null)
				stack.POPn(EBCRegisters.R5, false, null)
				procedure.ADD64(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				stack.PUSHn(EBCRegisters.R5, false, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"address",
				MethodTypeDesc.ofDescriptor("()J")
			) to { procedure, stack, _ ->
				procedure.MOVIqw(EBCRegisters.R6, false, null, 0u)
				stack.POPn(EBCRegisters.R6, false, null)
				stack.PUSH64(EBCRegisters.R6, false, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"get",
				MethodTypeDesc.ofDescriptor("(Ljava/lang/foreign/AddressLayout;J)Ljava/lang/foreign/MemorySegment;")
			) to { procedure, stack, _ ->
				stack.POP64(EBCRegisters.R6, false, null)
				// TODO: Handle AddressLayout
				stack.POPn(EBCRegisters.R5, false, null)
				procedure.ADD64(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				stack.PUSHn(EBCRegisters.R5, true, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"get",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfLong;J)J")
			) to { procedure, stack, _ ->
				stack.POP64(EBCRegisters.R6, false, null)
				// TODO: Handle ValueLayout
				stack.POPn(EBCRegisters.R5, false, null)
				procedure.ADD64(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				stack.PUSH64(EBCRegisters.R5, true, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"get",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfInt;J)I")
			) to { procedure, stack, _ ->
				stack.POP64(EBCRegisters.R6, false, null)
				// TODO: Handle ValueLayout
				stack.POPn(EBCRegisters.R5, false, null)
				procedure.ADD64(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				stack.PUSH32(EBCRegisters.R5, true, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"get",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfShort;J)S")
			) to { procedure, stack, _ ->
				stack.POP64(EBCRegisters.R6, false, null)
				// TODO: Handle ValueLayout
				stack.POPn(EBCRegisters.R5, false, null)
				procedure.ADD64(
					EBCRegisters.R5, false,
					EBCRegisters.R6, false, null
				)
				procedure.MOVIdd(EBCRegisters.R6, false, null, 0u)
				procedure.MOVdw(
					EBCRegisters.R6, false, null,
					EBCRegisters.R5, true, null
				)
				stack.PUSH32(EBCRegisters.R6, false, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"set",
				MethodTypeDesc.ofDescriptor("(Ljava/lang/foreign/AddressLayout;JLjava/lang/foreign/MemorySegment;)V")
			) to { procedure, stack, _ ->
				stack.POPn(EBCRegisters.R6, false, null)
				stack.POP64(EBCRegisters.R5, false, null)
				// TODO: Handle AddressLayout
				stack.POPn(EBCRegisters.R4, false, null)
				procedure.ADD64(
					EBCRegisters.R4, false,
					EBCRegisters.R5, false, null
				)
				procedure.MOVnw(
					EBCRegisters.R4, true,
					EBCRegisters.R6, false,
					null, null
				)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"set",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfLong;JJ)V")
			) to { procedure, stack, _ ->
				stack.POP64(EBCRegisters.R6, false, null)
				stack.POP64(EBCRegisters.R5, false, null)
				// TODO: Handle AddressLayout
				stack.POPn(EBCRegisters.R4, false, null)
				procedure.ADD64(
					EBCRegisters.R4, false,
					EBCRegisters.R5, false, null
				)
				procedure.MOVqw(
					EBCRegisters.R4, true, null,
					EBCRegisters.R6, false, null
				)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"set",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfInt;JI)V")
			) to { procedure, stack, _ ->
				stack.POP32(EBCRegisters.R6, false, null)
				stack.POP64(EBCRegisters.R5, false, null)
				// TODO: Handle AddressLayout
				stack.POPn(EBCRegisters.R4, false, null)
				procedure.ADD64(
					EBCRegisters.R4, false,
					EBCRegisters.R5, false, null
				)
				procedure.MOVdw(
					EBCRegisters.R4, true, null,
					EBCRegisters.R6, false, null
				)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"set",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfShort;JS)V")
			) to { procedure, stack, _ ->
				stack.POP32(EBCRegisters.R6, false, null)
				stack.POP64(EBCRegisters.R5, false, null)
				// TODO: Handle AddressLayout
				stack.POPn(EBCRegisters.R4, false, null)
				procedure.ADD64(
					EBCRegisters.R4, false,
					EBCRegisters.R5, false, null
				)
				procedure.MOVww(
					EBCRegisters.R4, true, null,
					EBCRegisters.R6, false, null
				)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.SPECIAL, owner,
				"set",
				MethodTypeDesc.ofDescriptor($$"(Ljava/lang/foreign/ValueLayout$OfByte;JB)V")
			) to { procedure, stack, _ ->
				stack.POP32(EBCRegisters.R6, false, null)
				stack.POP64(EBCRegisters.R5, false, null)
				// TODO: Handle AddressLayout
				stack.POPn(EBCRegisters.R4, false, null)
				procedure.ADD64(
					EBCRegisters.R4, false,
					EBCRegisters.R5, false, null
				)
				procedure.MOVbw(
					EBCRegisters.R4, true, null,
					EBCRegisters.R6, false, null
				)
			}
		)
}