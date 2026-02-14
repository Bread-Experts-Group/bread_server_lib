package org.bread_experts_group.generic.numeric

val ULong.coercedInt: Int
	get() = this.coerceAtMost(Int.MAX_VALUE.toULong()).toInt()