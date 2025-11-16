package org.bread_experts_group.api.feature

interface CheckedImplementation : Implementation {
	fun supported(): Boolean
}