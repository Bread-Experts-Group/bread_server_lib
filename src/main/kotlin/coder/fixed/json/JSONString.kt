package org.bread_experts_group.coder.fixed.json

data class JSONString(val value: String) : JSONElement() {
	override fun toString(): String = "\"$value\""
}