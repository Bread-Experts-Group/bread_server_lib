package org.bread_experts_group.generic

data class MappedEnumeration<M, T>(
	val enum: T?,
	val raw: M
) where T : Enum<T>, T : Mappable<T, M> {
	constructor(enum: T) : this(enum, enum.id)
	constructor(raw: M) : this(null, raw)

	override fun toString(): String = enum?.toString() ?: "Other [$raw]"
}