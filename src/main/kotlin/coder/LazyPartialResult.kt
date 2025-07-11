package org.bread_experts_group.coder

class LazyPartialResult<T, E>(
	val result: T?,
	val thrown: E?
) where E : Throwable {
	val resultSafe: T
		get() {
			if (thrown != null) throw thrown
			return result!!
		}

	override fun toString(): String = "LazyPartialResult[" + buildList {
		if (result != null) add("OK: $result")
		if (thrown != null) add("ERR: $thrown")
	}.joinToString(", ") + ']'
}