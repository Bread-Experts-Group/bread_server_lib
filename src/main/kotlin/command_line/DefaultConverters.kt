package org.bread_experts_group.command_line

import java.net.URI

fun stringToLong(str: String, between: LongRange = Long.MIN_VALUE..Long.MAX_VALUE): Long {
	val read = if (str.substring(0, 1) == "0x") str.substring(2).toLong(16)
	else if (str.substring(0, 1) == "0b") str.substring(2).toLong(2)
	else str.toLong()
	if (read !in between) throw IllegalArgumentException("\"$read\" out of range, must be between $between")
	return read
}

fun stringToURI(str: String): URI = URI(str)
fun stringToInt(str: String): Int = stringToLong(str).toInt()
fun stringToBoolean(str: String): Boolean = str.lowercase().let { it == "true" || it == "yes" || it == "1" }