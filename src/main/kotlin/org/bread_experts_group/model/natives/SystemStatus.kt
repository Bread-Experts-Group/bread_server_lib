package org.bread_experts_group.model.natives

sealed class SystemStatus(message: String?) : RuntimeException(message) {
	open class OK(message: String?) : SystemStatus(message)
	open class Error(message: String?) : SystemStatus(message)
	open class Undefined(message: String?) : SystemStatus(message)
}