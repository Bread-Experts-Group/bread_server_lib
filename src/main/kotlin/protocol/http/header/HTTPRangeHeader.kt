package org.bread_experts_group.protocol.http.header

class HTTPRangeHeader(
	val totalSize: Long,
	val ranges: List<Pair<Long, Long>>
) {
	companion object {
		fun parse(value: String, length: Long): HTTPRangeHeader {
			val (unit, values) = value.split('=')
			if (unit.lowercase() != "bytes") throw UnsupportedOperationException("HTTP Range unit \"$unit\"")
			var size = 0L
			val ranges = buildList {
				values.split(Regex(", ?")).forEach { value ->
					val (from, to) = value.split('-', ignoreCase = true)
					var fromL = if (from.isEmpty()) -1L else from.toLong()
					var toL = if (to.isEmpty()) (length - 1) else to.toLong()
					if (fromL == -1L) {
						size += toL
						val adjLength = length - 1
						fromL = adjLength - (toL - 1)
						toL = adjLength
					} else {
						size += (toL - fromL) + 1L
					}
					add(fromL to toL)
				}
			}
			return HTTPRangeHeader(size, ranges)
		}
	}
}