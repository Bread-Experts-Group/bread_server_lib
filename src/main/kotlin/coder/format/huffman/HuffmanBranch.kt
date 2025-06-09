package org.bread_experts_group.coder.format.huffman

import org.bread_experts_group.stream.FailQuickInputStream

class HuffmanBranch<T> {
	private val branches = HashMap<Boolean, Any>(2)
	fun branch(on: Boolean): HuffmanBranch<T> {
		if (branches.containsKey(on)) throw UnsupportedOperationException("[$on] already set!")
		return HuffmanBranch<T>().also { branches[on] = it as Any }
	}

	fun edge(on: Boolean, with: T) {
		if (branches.containsKey(on)) throw UnsupportedOperationException("[$on] already set!")
		branches[on] = with as Any
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

	var nextCache: T? = null
	fun next(from: BitInputStream): T {
		if (nextCache != null) {
			val saved = nextCache
			nextCache = readNext(from)
			return saved!!
		} else {
			val saved = readNext(from)
			if (saved == null) throw FailQuickInputStream.EndOfStream()
			nextCache = readNext(from)
			return saved
		}
	}
}