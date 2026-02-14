package org.bread_experts_group.api.compile.ebc.dynamic

import org.bread_experts_group.api.compile.ebc.EBCCompilerData
import org.bread_experts_group.api.compile.ebc.EBCProcedure
import java.lang.constant.ConstantDesc
import java.lang.constant.MethodHandleDesc

interface KotlinEBCDynamicProvider {
	fun dynamics(): Map<MethodHandleDesc, (EBCProcedure, EBCCompilerData, List<ConstantDesc>) -> Unit>
}