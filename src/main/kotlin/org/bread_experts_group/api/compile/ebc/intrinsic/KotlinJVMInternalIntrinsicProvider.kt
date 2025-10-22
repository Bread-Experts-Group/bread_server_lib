package org.bread_experts_group.api.compile.ebc.intrinsic

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import org.bread_experts_group.api.compile.ebc.EBCRegisters
import org.bread_experts_group.api.compile.ebc.EBCStackTracker
import java.lang.constant.ClassDesc
import java.lang.constant.DirectMethodHandleDesc
import java.lang.constant.MethodHandleDesc
import java.lang.constant.MethodTypeDesc

class KotlinJVMInternalIntrinsicProvider : KotlinEBCIntrinsicProvider {
	private val owner = ClassDesc.ofInternalName("kotlin/jvm/internal/Intrinsics")
	override fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCStackTracker, EBCCompilerData) -> Unit> =
		mapOf(
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.STATIC, owner,
				"checkNotNullParameter",
				MethodTypeDesc.ofDescriptor("(Ljava/lang/Object;Ljava/lang/String;)V")
			) to { _, stack, _ ->
				stack.POPn(EBCRegisters.R6, false, null)
				stack.POPn(EBCRegisters.R6, false, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.STATIC, owner,
				"checkNotNullExpressionValue",
				MethodTypeDesc.ofDescriptor("(Ljava/lang/Object;Ljava/lang/String;)V")
			) to { _, stack, _ ->
				stack.POPn(EBCRegisters.R6, false, null)
				stack.POPn(EBCRegisters.R6, false, null)
			},
			MethodHandleDesc.ofMethod(
				DirectMethodHandleDesc.Kind.STATIC, owner,
				"checkNotNull",
				MethodTypeDesc.ofDescriptor("(Ljava/lang/Object;Ljava/lang/String;)V")
			) to { _, stack, _ ->
				stack.POPn(EBCRegisters.R6, false, null)
				stack.POPn(EBCRegisters.R6, false, null)
			},
		)
}