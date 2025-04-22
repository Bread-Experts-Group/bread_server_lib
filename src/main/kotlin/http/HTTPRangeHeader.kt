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
				values.split(Regex(", ?")).forEach { value ->
					val (from, to) = value.split('-', ignoreCase = true)
					var fromL = if (from.isEmpty()) -1L else from.toLong()
					var toL = (if (to.isEmpty()) file.length() else to.toLong()) - 1
					if (fromL == -1L) {
						fromL = file.length() - toL
						toL = file.length() - 1
					}
					size += (toL - fromL) + 1L
					add(fromL to toL)
				}
			}
			return HTTPRangeHeader(size, ranges)
		}
	}
}