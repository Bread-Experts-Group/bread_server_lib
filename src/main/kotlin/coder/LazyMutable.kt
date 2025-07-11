package org.bread_experts_group.coder

import kotlin.reflect.KProperty

class LazyMutable<T>(private val initializer: () -> T) {
	private var initialized = false
	private var value: T? = null
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
		if (!initialized && value == null) {
			initialized = true
			value = initializer()
		}
		@Suppress("UNCHECKED_CAST")
		return value as T
	}

	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
		initialized = true
		this.value = value
	}
}