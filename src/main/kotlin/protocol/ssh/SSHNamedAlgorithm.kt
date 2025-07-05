package org.bread_experts_group.protocol.ssh

open class SSHNamedAlgorithm(val base: String) {
	override fun toString(): String = "[$base]"
	open fun stringForm() = base
	override fun equals(other: Any?): Boolean {
		if (other !is SSHNamedAlgorithm) return false
		return other.base == this.base
	}

	override fun hashCode(): Int = base.hashCode()
}