package org.bread_experts_group.model.natives

import java.util.*

class IndexedEnumSet<E : Enum<E>>(clazz: Class<E>) : MutableSet<E>, List<E> {
	companion object {
		fun <E : Enum<E>> noneOf(enumClass: Class<E>): IndexedEnumSet<E> {
			return IndexedEnumSet(enumClass)
		}

		@Suppress("UNCHECKED_CAST")
		fun <E : Enum<E>> of(a: E, b: E, c: E): IndexedEnumSet<E> {
			val new = IndexedEnumSet(a::class.java as Class<out Enum<*>>) as IndexedEnumSet<E>
			new.add(a)
			new.add(b)
			new.add(c)
			return new
		}
	}

	private val constants = clazz.enumConstants
	private val bits = BitSet(constants.size)

	override fun iterator(): MutableIterator<E> = object : MutableIterator<E> {
		var position: Int = -1
		fun nextEntry() {
			while (position++ < constants.size) {
				if (bits[position]) return
			}
			position = -1
		}

		init {
			nextEntry()
		}

		override fun hasNext(): Boolean = position != -1

		override fun next(): E {
			val enum = constants[position]
			nextEntry()
			return enum
		}

		override fun remove() {
			TODO("Not yet implemented")
		}
	}

	override fun add(element: E): Boolean {
		val index = constants.indexOf(element)
		if (bits[index]) return false
		bits[index] = true
		return true
	}

	override fun remove(element: E): Boolean {
		TODO("Not yet implemented")
	}

	override fun addAll(elements: Collection<E>): Boolean {
		TODO("Not yet implemented")
	}

	override fun removeAll(elements: Collection<E>): Boolean {
		TODO("Not yet implemented")
	}

	override fun retainAll(elements: Collection<E>): Boolean {
		TODO("Not yet implemented")
	}

	override fun clear() {
		TODO("Not yet implemented")
	}

	override fun listIterator(): MutableListIterator<E> {
		TODO("Not yet implemented")
	}

	override fun listIterator(index: Int): MutableListIterator<E> {
		TODO("Not yet implemented")
	}

	override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> {
		TODO("Not yet implemented")
	}

	override val size: Int
		get() {
			var size = 0
			while (true) {
				val next = bits.nextSetBit(size)
				if (next == -1) return size
				size = next
			}
		}

	override fun isEmpty(): Boolean {
		TODO("Not yet implemented")
	}

	override fun contains(element: E): Boolean {
		TODO("Not yet implemented")
	}

	override fun containsAll(elements: Collection<E>): Boolean {
		TODO("Not yet implemented")
	}

	override fun spliterator(): Spliterator<E?> {
		TODO("Not yet implemented")
	}

	override fun get(index: Int): E {
		TODO("Not yet implemented")
	}

	override fun indexOf(element: E): Int {
		TODO("Not yet implemented")
	}

	override fun lastIndexOf(element: E): Int {
		TODO("Not yet implemented")
	}

	override fun toString(): String = "[${this.joinToString(", ")}]"
}