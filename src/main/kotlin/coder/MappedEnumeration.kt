package org.bread_experts_group.coder

data class MappedEnumeration<M, T>(
	val enum: T,
	val raw: M
) where T : Enum<T>, T : Mappable<T, M> {
	override fun toString(): String = if (enum == enum.other()) "Other [$raw]" else enum.toString()
}