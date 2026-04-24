package org.bread_experts_group.api.compile.ebc

sealed interface LocalVariableElement {
	data class Reference(val clazz: Class<*>, var isNull: Boolean?) : LocalVariableElement
}