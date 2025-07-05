package org.bread_experts_group.protocol.ssh

import java.net.URI

class SSHPrivateAlgorithm(base: String, val domain: URI) : SSHNamedAlgorithm(base) {
	override fun toString(): String = "[$base]@$domain"
	override fun stringForm(): String = "$base@$domain"
	override fun equals(other: Any?): Boolean {
		if (other !is SSHPrivateAlgorithm) return false
		return other.base == this.base && other.domain == this.domain
	}

	override fun hashCode(): Int = domain.hashCode() * base.hashCode()
}