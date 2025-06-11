package org.bread_experts_group

import java.util.*
import kotlin.time.Duration.Companion.seconds

fun hexLen(s: String, n: Int) = "0x${s.uppercase().padStart(n, '0')}"
fun hex(value: Long): String = hexLen(value.toString(16), 16)
fun hex(value: ULong): String = hexLen(value.toString(16), 16)
fun hex(value: Int): String = hexLen(value.toString(16), 8)
fun hex(value: UInt): String = hexLen(value.toString(16), 8)
fun hex(value: Short): String = hexLen(value.toString(16), 4)
fun hex(value: UShort): String = hexLen(value.toString(16), 4)
fun hex(value: Byte): String = hexLen(value.toString(16), 2)
fun hex(value: UByte): String = hexLen(value.toString(16), 2)

val siKeys = listOf(" k", " M", " G", " T", " P", " E")
val siIntervals = listOf(0.0, 1000.0, 1e+6, 1e+9, 1e+12, 1e+15, 1e+18).map { it.toLong() }
fun truncateSI(n: Long, decimals: Int = 2): String {
	val intIdx = siIntervals.indexOf(siIntervals.firstOrNull { it > n } ?: siIntervals.last())
	val interval = siIntervals[intIdx - 1]
	return String.format(
		"%.0${decimals}f${siKeys.getOrNull(intIdx - 2) ?: ""}",
		if (interval > 0) (n.toDouble() / interval) else n.toDouble()
	)
}

// Thank you, Donato Wolfisberg @ https://stackoverflow.com/a/74301065
fun formatDurationTime(durationSeconds: Double) = durationSeconds.seconds.toComponents { hours, minutes, seconds, ns ->
	String.format(
		Locale.getDefault(),
		"%02d:%02d:%05.5f",
		hours,
		minutes,
		seconds + (ns / 1000000000.0)
	)
}