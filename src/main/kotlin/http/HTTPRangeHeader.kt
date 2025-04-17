package org.bread_experts_group.http

import java.io.File

class HTTPRangeHeader(
	val totalSize: Long,
	val ranges: List<Pair<Long, Long>>
) {
	companion object {
		fun parse(value: String, file: File): HTTPRangeHeader {
			val (unit, values) = value.split('=')
			if (unit.lowercase() != "bytes") throw UnsupportedOperationException("HTTP Range unit \"$unit\"")
			var size = 0L
			var ranges = buildList {
				println(values)
				values.split(Regex(", ?")).forEach { value ->
					val (from, to) = value.split('-', ignoreCase = true)
					val parsed = Pair(
						if (from.isEmpty()) -1 else from.toLong(),
						if (to.isEmpty()) file.length() else to.toLong(),
					)
					size += if (parsed.first == -1L) file.length() - parsed.second
					else parsed.second - parsed.first
					add(parsed)
				}
			}
			return HTTPRangeHeader(size, ranges)
		}
	}
}