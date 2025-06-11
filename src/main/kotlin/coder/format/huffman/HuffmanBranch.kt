package org.bread_experts_group.coder.format.huffman

import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.readExtensibleULong
import org.bread_experts_group.stream.writeExtensibleULong
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class HuffmanBranch<T> private constructor(
	val directMap: MutableMap<T, List<Boolean>>,
	val position: List<Boolean>
) {
	constructor() : this(mutableMapOf(), emptyList())

	companion object {
		val consolidatoryComparator = Comparator<Pair<*, Int>> { (_, f1), (_, f2) -> f1.compareTo(f2) }
		fun <T> consolidate(priorities: Collection<Pair<T, Int>>): List<*> {
			val queue = PriorityQueue(priorities.size, consolidatoryComparator)
			val secondaryQueue = PriorityQueue(priorities.size, consolidatoryComparator)
			queue.addAll(priorities)
			while (queue.size > 2) {
				while (queue.size > 2) {
					val (valueA, countA) = queue.poll()
					val (valueB, countB) = queue.poll()
					secondaryQueue.add(listOf(valueA, valueB) to (countA + countB))
				}
				queue.addAll(secondaryQueue)
				secondaryQueue.clear()
			}
			return listOf((queue.poll() as Pair<*, *>).first, (queue.poll() as Pair<*, *>).first)
		}

		fun <T> compute(from: Iterable<T>): HuffmanBranch<T> {
			@Suppress("UNCHECKED_CAST")
			fun branch(branches: List<*>, huffmanBranch: HuffmanBranch<T>) {
				val (left, right) = branches
				if (left is List<*>) branch(left, huffmanBranch.branch(false))
				else huffmanBranch.edge(false, left as T)
				if (right is List<*>) branch(right, huffmanBranch.branch(true))
				else huffmanBranch.edge(true, right as T)
			}

			val root = HuffmanBranch<T>()
			branch(consolidate(from.groupingBy { it }.eachCount().map { it.key to it.value }), root)
			return root
		}

		// TODO: USE EXTENSIBLE INTEGERS MORE EFFECTIVELY HERE: USE SHIFTING & ORING
		fun HuffmanBranch<Char>.export(
			to: OutputStream,
			stringValue: String = ""
		): Unit = branches.forEach { (key, value) ->
			@Suppress("UNCHECKED_CAST")
			if (value is HuffmanBranch<*>) (value as HuffmanBranch<Char>).export(
				to,
				stringValue + (if (key) '1' else '0')
			) else {
				to.writeExtensibleULong((value as Char).code.toULong())
				to.writeExtensibleULong("1$stringValue${if (key) '1' else '0'}".toULong(2))
			}
		}

		fun import(from: InputStream): HuffmanBranch<Char> {
			val branch = HuffmanBranch<Char>()
			while (from.available() > 0) {
				val value = Char(from.readExtensibleULong().toInt())
				val importing = from.readExtensibleULong().toString(2).substring(1)
				var currentBranch = branch
				for (position in importing.take(importing.length - 1)) {
					currentBranch = currentBranch.branch(position == '1')
				}
				currentBranch.edge(importing[importing.length - 1] == '1', value)
			}
			return branch
		}
	}

	private val branches = HashMap<Boolean, Any>(2)

	fun visualize(from: String = ""): String = buildString {
		branches.forEach { (key, value) ->
			if (value is HuffmanBranch<T>) append(value.visualize(from + (if (key) '1' else '0')))
			else append("$value: $from${if (key) '1' else '0'}\n")
		}
	}

	fun branch(on: Boolean): HuffmanBranch<T> {
		val check = branches[on]
		if (check != null) {
			if (check !is HuffmanBranch<T>) throw UnsupportedOperationException("[$on] already set for [$check]!")
			return check
		}
		return HuffmanBranch(this.directMap, this.position + on).also { branches[on] = it as Any }
	}

	fun edge(on: Boolean, with: T) {
		if (branches.containsKey(on)) throw UnsupportedOperationException("[$on] already set for [${branches[on]}]!")
		branches[on] = with as Any
		directMap[with] = position + on
	}

	private fun readNext(from: BitInputStream): T? {
		var branch = this
		var bitChain = ""
		while (true) {
			val bit = from.nextBit()
			bitChain += if (bit) '1' else '0'
			try {
				val selected = branch.branches.getValue(bit)
				if (selected is HuffmanBranch<T>) branch = selected
				else (@Suppress("UNCHECKED_CAST") return selected as T)
			} catch (_: NoSuchElementException) {
				if (from.available() > 0) throw NoSuchElementException("Missing branch/edge for chain [$bitChain]")
				else return null
			}
		}
	}

	fun next(from: BitInputStream): T = readNext(from) ?: throw FailQuickInputStream.EndOfStream()

	fun write(what: T, to: BitOutputStream<*>) {
		directMap.getValue(what).forEach { to.writeBit(it) }
	}
}