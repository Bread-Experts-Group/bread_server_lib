package org.bread_experts_group.api.compile.ebc

import java.lang.constant.ClassDesc

sealed interface LocalVariableElement {
	data class Reference(val clazz: ClassDesc, var isNull: Boolean?) : LocalVariableElement
}