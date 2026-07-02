package org.bread_experts_group.generic

import kotlin.reflect.KClass

@ConsistentCopyVisibility
data class MappedEnumeration<M, T> private constructor(
	val enum: T?,
	val raw: M
) where T : Enum<T>, T : Mappable<T, M> {
	companion object {
		inline fun <reified T, M> raw(raw: M) where T : Enum<T>, T : Mappable<T, M> = MappedEnumeration(T::class, raw)
	}

	constructor(enum: T) : this(enum, enum.id)
	constructor(enumClass: KClass<T>, raw: M) : this(
		enumClass.java.enumConstants.firstOrNull { (it as T).id == raw },
		raw
	)

	constructor(enumClass: Class<T>, raw: M) : this(enumClass.kotlin, raw)

	override fun toString(): String = enum?.toString() ?: "Other [$raw]"
}