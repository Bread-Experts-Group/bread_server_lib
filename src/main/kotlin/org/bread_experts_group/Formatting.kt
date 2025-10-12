package org.bread_experts_group

import java.util.*
import kotlin.math.pow
import kotlin.time.Duration

fun hexLen(s: String, n: Int): String = "0x${s.uppercase().padStart(n, '0')}"
fun hex(value: Long): String = hexLen(value.toString(16), 16)
fun hex(value: ULong): String = hexLen(value.toString(16), 16)
fun hex(value: Int): String = hexLen(value.toString(16), 8)
fun hex(value: UInt): String = hexLen(value.toString(16), 8)
fun hex(value: Short): String = hexLen(value.toString(16), 4)
fun hex(value: UShort): String = hexLen(value.toString(16), 4)
fun hex(value: Byte): String = hexLen(value.toString(16), 2)
fun hex(value: UByte): String = hexLen(value.toString(16), 2)

val si: List<Pair<String, Double>> = listOf(
	"Q" to 10.0.pow(30.0),
	"R" to 10.0.pow(27.0),
	"Y" to 10.0.pow(24.0),
	"Z" to 10.0.pow(21.0),
	"E" to 10.0.pow(18.0),
	"P" to 10.0.pow(15.0),
	"T" to 10.0.pow(12.0),
	"G" to 10.0.pow(9.0),
	"M" to 10.0.pow(6.0),
	"k" to 10.0.pow(3.0),
	"h" to 10.0.pow(2.0),
	"da" to 10.0.pow(1.0),
	"" to 1.0,
	"d" to 10.0.pow(-1.0),
	"c" to 10.0.pow(-2.0),
	"m" to 10.0.pow(-3.0),
	"μ" to 10.0.pow(-6.0),
	"n" to 10.0.pow(-9.0),
	"p" to 10.0.pow(-12.0),
	"f" to 10.0.pow(-15.0),
	"a" to 10.0.pow(-18.0),
	"z" to 10.0.pow(-21.0),
	"y" to 10.0.pow(-24.0),
	"r" to 10.0.pow(-27.0),
	"q" to 10.0.pow(-30.0)
)

fun normalize(n: Int, to: Int): Int = ((n / to) + (if ((n % to) != 0) 1 else 0)) * to
fun normalize(n: UInt, to: UInt): UInt = ((n / to) + (if ((n % to) != 0u) 1u else 0u)) * to

fun Double.formatMetric(decimals: Int = 2): String {
	val (sign, divisor) = si.firstOrNull { this >= it.second } ?: si.last()
	return String.format(
		"%.0${decimals}f $sign",
		this / divisor
	)
}

// Thank you, Donato Wolfisberg @ https://stackoverflow.com/a/74301065
fun Duration.formatTime(): String = this.toComponents { hours, minutes, seconds, ns ->
	String.format(
		Locale.getDefault(),
		"%02d:%02d:%05.5f",
		hours,
		minutes,
		seconds + (ns / 1000000000.0)
	)
}