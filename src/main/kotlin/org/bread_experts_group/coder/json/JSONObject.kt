package org.bread_experts_group.coder.json

import org.bread_experts_group.coder.json.JSONElement.Companion
import java.math.BigDecimal

data class JSONObject(
	val entries: MutableMap<String, JSONElement>
) : JSONElement() {
	constructor(vararg entries: Pair<String, JSONElement>) : this(mapOf(*entries).toMutableMap())

	fun <T> inObject(key: String, init: JSONObject.() -> T): T = entries.getValue(key).asObject { init() }
	inline fun <reified T> inArray(key: String, crossinline init: JSONArray.(JSONElement) -> T): Array<T> =
		entries.getValue(key).asArray {
			entries.map { init(this, it) }.toTypedArray()
		}

	fun withNumber(key: String): BigDecimal = entries.getValue(key).asNumber { this.value }
	fun withString(key: String): String = entries.getValue(key).asString { this.value }
	fun withObject(key: String): JSONObject = entries.getValue(key).asObject { this }
	fun withArray(key: String): JSONArray = entries.getValue(key).asArray { this }

	override fun toString(): String = '{' + entries.entries.joinToString(",") { (key, value) ->
		"\"$key\":$value"
	} + '}'

	companion object {
		fun localRead(stream: TrackingBufferedReader): JSONObject {
			val newEntries = mutableMapOf<String, JSONElement>()
			try {
				while (true) {
					read(stream).asString {
						while (stream.readCharNoCache() != ':') continue
						newEntries[this.value] = read(stream)
					}
				}
			} catch (_: Companion.ObjectExit) {
			}
			return JSONObject(newEntries.toMutableMap())
		}
	}
}