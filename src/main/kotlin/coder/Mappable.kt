package org.bread_experts_group.coder

import kotlin.enums.EnumEntries

interface Mappable<E, T> where E : Enum<E>, E : Mappable<E, T> {
	val id: T
	val tag: String

	fun stringForm(): String = "$tag [$id]"
	fun other(): E? = null

	companion object {
		inline fun <reified E, T> EnumEntries<E>.id(n: T): E where E : Enum<E>, E : Mappable<E, T> = this.firstOrNull {
			it.id == n
		} ?: this.first().other() ?: throw IndexOutOfBoundsException("Missing ID for $n")
	}
}