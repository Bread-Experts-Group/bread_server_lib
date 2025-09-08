package org.bread_experts_group.coder

import java.util.*
import kotlin.enums.EnumEntries

interface Flaggable {
	val position: Long

	companion object {
		fun Array<out Flaggable>.raw() = this.fold(0L) { acc, flag -> acc or flag.position }
		fun Collection<Flaggable>.raw() = this.fold(0L) { acc, flag -> acc or flag.position }
		fun <E> EnumEntries<E>.from(n: UByte) where E : Enum<E>, E : Flaggable = this.from(n.toLong())
		fun <E> EnumEntries<E>.from(n: Int) where E : Enum<E>, E : Flaggable = this.from(n.toLong())
		fun <E> EnumEntries<E>.from(n: Long): EnumSet<E> where E : Enum<E>, E : Flaggable {
			val filtered = this.filter { it.position and n != 0L }
			return if (filtered.isEmpty()) EnumSet.noneOf(this.first()::class.java as Class<E>)
			else EnumSet.copyOf(filtered)
		}
	}
}