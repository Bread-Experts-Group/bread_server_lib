package org.bread_experts_group.generic

import kotlin.enums.EnumEntries

interface Mappable<E, T> where E : Enum<E>, E : Mappable<E, T> {
	val id: T
	val tag: String

	fun stringForm(): String = "$tag [$id]"

	companion object {
		inline fun <reified E, T> EnumEntries<E>.id(
			n: T
		): MappedEnumeration<T, E> where E : Enum<E>, E : Mappable<E, T> = MappedEnumeration(
			this.firstOrNull {
				it.id == n
			},
			n
		)
	}
}