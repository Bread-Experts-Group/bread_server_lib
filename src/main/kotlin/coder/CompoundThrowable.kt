package org.bread_experts_group.coder

class CompoundThrowable<E : Throwable> {
	val thrown = mutableListOf<Throwable>()
	fun addThrown(e: E) = thrown.add(e)
	fun build(): Throwable? =
		if (thrown.isNotEmpty()) Exception("Multiple issues [${thrown.size}]$thrown", thrown.first())
		else null
}