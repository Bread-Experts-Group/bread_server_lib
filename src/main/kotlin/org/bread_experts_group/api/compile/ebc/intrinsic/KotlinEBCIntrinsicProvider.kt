package org.bread_experts_group.api.compile.ebc.intrinsic

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import java.lang.constant.MethodHandleDesc

interface KotlinEBCIntrinsicProvider {
	fun intrinsics(): Map<MethodHandleDesc, (EBCProcedure, EBCCompilerData) -> Unit>
}