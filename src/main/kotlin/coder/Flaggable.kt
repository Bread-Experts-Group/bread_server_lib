package org.bread_experts_group.coder

import kotlin.enums.EnumEntries

interface Flaggable {
	val position: Long
	fun present(n: Long) = n and this.position == this.position
	fun present(n: Int) = present(n.toLong())

	companion object {
		fun Collection<Flaggable>.raw() = this.fold(0L) { acc, flag -> acc or flag.position }
		fun <E> Collection<E>.allPresent(vararg flag: E) where E : Enum<E>, E : Flaggable = this.containsAll(
			flag.toList()
		)

		fun <E> EnumEntries<E>.from(n: Int) where E : Enum<E>, E : Flaggable = this.from(n.toLong())
		fun <E> EnumEntries<E>.from(n: Long) where E : Enum<E>, E : Flaggable = this.filter {
			it.position and n != 0L
		}.toSet()
	}
}