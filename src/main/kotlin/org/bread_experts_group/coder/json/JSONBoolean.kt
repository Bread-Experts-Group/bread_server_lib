package org.bread_experts_group.coder.json

data class JSONBoolean(val value: Boolean) : JSONElement() {
	override fun toString(): String = if (value) "true" else "false"
}