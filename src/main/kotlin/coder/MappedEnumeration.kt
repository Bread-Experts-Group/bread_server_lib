package org.bread_experts_group.coder

data class MappedEnumeration<M, T>(
	val enum: T?,
	val raw: M
) where T : Enum<T>, T : Mappable<T, M> {
	constructor(enum: T) : this(enum, enum.id)

	override fun toString(): String = enum?.toString() ?: "Other [$raw]"
}