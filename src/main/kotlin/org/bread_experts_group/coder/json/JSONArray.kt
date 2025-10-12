package org.bread_experts_group.coder.json

import org.bread_experts_group.coder.json.JSONElement.Companion

data class JSONArray(
	val entries: Array<JSONElement>
) : JSONElement() {
	inline fun <reified T> mapped(init: JSONArray.(JSONElement) -> T): Array<T> = entries
		.map { init(this, it) }
		.toTypedArray()

	companion object {
		fun localRead(stream: TrackingBufferedReader): JSONArray {
			val entries = mutableListOf<JSONElement>()
			try {
				while (true) entries.add(read(stream))
			} catch (_: Companion.ArrayExit) {
			}
			return JSONArray(entries.toTypedArray())
		}
	}

	override fun toString(): String = '[' + entries.joinToString(",") { it.toString() } + ']'

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as JSONArray
		return entries.contentEquals(other.entries)
	}

	override fun hashCode(): Int {
		return entries.contentHashCode()
	}
}