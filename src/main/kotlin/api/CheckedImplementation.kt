package org.bread_experts_group.api

interface CheckedImplementation : Implementation {
	fun supported(): Boolean
}