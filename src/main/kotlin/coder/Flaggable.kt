package org.bread_experts_group.coder

interface Flaggable {
	val position: Long
	fun present(n: Long) = n and this.position == this.position
	fun present(n: Int) = present(n.toLong())
}