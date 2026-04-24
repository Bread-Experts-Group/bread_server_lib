package org.bread_experts_group.api.compile.ebc

data class ExecutionFrame(
	val variables: MutableMap<Int, LocalVariableElement>,
	val stack: MutableList<StackElement> = mutableListOf()
) {
	override fun equals(other: Any?): Boolean {
		if (other !is ExecutionFrame) return false
		if (this.variables != other.variables) return false
		if (this.stack != other.stack) return false
		return true
	}
}