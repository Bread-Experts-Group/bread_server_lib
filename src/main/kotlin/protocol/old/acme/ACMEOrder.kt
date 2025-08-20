package org.bread_experts_group.protocol.old.acme

import org.bread_experts_group.coder.fixed.json.JSONConvertible

data class ACMEOrder(
	val type: String,
	val value: String
) : JSONConvertible {
	override fun toJSON(): String = buildString {
		append('{')
		append("\"type\":\"$type\",")
		append("\"value\":\"$value\"")
		append('}')
	}
}