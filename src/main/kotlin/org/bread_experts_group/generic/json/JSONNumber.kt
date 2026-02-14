package org.bread_experts_group.generic.json

import java.math.BigDecimal

data class JSONNumber(val value: BigDecimal) : JSONElement() {
	override fun toString(): String = value.toString()
}