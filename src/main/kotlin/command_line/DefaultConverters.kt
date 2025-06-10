package org.bread_experts_group.command_line

fun stringToLong(between: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): (String) -> Long = {
	val numeric = if (it.contains('#', true)) {
		val (base, integer, empty) = it.split('#', ignoreCase = true, limit = 3)
		require(empty.isEmpty()) { "There shouldn't be any leading characters after the last '#' in [$it]" }
		integer.toLong(base.toInt())
	} else {
		val prefix = it.substring(0..1).lowercase()
		if (prefix == "0x") it.substring(2).toLong(16)
		else if (prefix == "0o") it.substring(2).toLong(8)
		else if (prefix == "0b") it.substring(2).toLong(2)
		else if (prefix.toIntOrNull() != null) it.toLong()
		else throw UnsupportedOperationException("Unknown basic prefix [$prefix] in [$it]")
	}
	if (numeric !in between) throw IllegalArgumentException("[$numeric] out of range, must be between [$between]")
	numeric
}

fun stringToInt(between: IntRange): (String) -> Int {
	val effector = stringToLong(between.start.toLong()..between.last)
	return { effector(it).toInt() }
}

fun stringToBoolean(str: String): Boolean = str.lowercase().let { it == "true" || it == "yes" || it == "1" }