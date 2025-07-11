package org.bread_experts_group.coder

import kotlin.reflect.KProperty

class SaveSingle<T>(initial: T? = null) {
	private var currently: T? = initial
	operator fun getValue(thisRef: Any?, property: KProperty<*>): T? {
		val saved = currently
		currently = null
		return saved
	}

	operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
		currently = value
	}
}