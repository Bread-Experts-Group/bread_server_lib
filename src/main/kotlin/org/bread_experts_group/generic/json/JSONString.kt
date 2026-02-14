package org.bread_experts_group.generic.json

data class JSONString(val value: String) : JSONElement() {
	override fun toString(): String = "\"$value\""
}