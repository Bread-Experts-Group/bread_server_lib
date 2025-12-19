package org.bread_experts_group.api.system.socket.system.windows

class WindowsEventValueContainer {
	var throwable: Throwable? = null
	var value: Any? = null
		get() = throwable?.let { throw it } ?: field
		set(value) {
			throwable?.let { throw it }
			field = value
		}
}