package org.bread_experts_group.http.header

/**
 * @author Miko Elbrecht
 * @since 2.50.0
 */
class HTTPAcceptHeader(val accepted: Map<String, Map<String, Double>>) {
	fun accepted(type: String): Double? {
		val types = type.split('/', limit = 2)
		return if (types.size == 2) {
			accepted[types[0]]?.let {
				val quality = it[types[1]] ?: it["*"]
				if (quality != null) return quality
			}
			if (types[0] == "*" || accepted.containsKey("*")) {
				for ((_, subTypes) in accepted) subTypes[types[1]]?.let { return it }
				accepted["*"]?.get("*")
			} else null
		} else accepted("*/${types[0]}")
	}

	companion object {
		fun parse(value: String): HTTPAcceptHeader {
			val accepted = mutableMapOf<String, MutableMap<String, Double>>()
			value.split(',').forEach {
				val bundle = it.split(';', limit = 2)
				val (mediaType, subType) = bundle[0].split('/', limit = 2)
				accepted.getOrPut(mediaType) { mutableMapOf() }[subType] =
					if (bundle.size == 2) bundle[1].substringAfter("q=").toDouble()
					else 1.0
			}
			return HTTPAcceptHeader(accepted)
		}
	}
}