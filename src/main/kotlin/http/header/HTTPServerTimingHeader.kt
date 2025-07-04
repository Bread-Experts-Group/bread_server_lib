package org.bread_experts_group.http.header

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

/**
 * @author Miko Elbrecht
 * @since 2.50.0
 */
class HTTPServerTimingHeader(val timeUnit: DurationUnit = DurationUnit.NANOSECONDS) {
	val timings = mutableListOf<HTTPServerTiming>()

	@OptIn(ExperimentalContracts::class)
	inline fun <R> time(tag: String, desc: String, lambda: () -> R): R {
		contract {
			callsInPlace(lambda, InvocationKind.EXACTLY_ONCE)
		}
		val (value, time) = measureTimedValue(lambda)
		timings.add(HTTPServerTiming(tag, desc, time))
		return value
	}

	override fun toString(): String = timings.map { timing ->
		val builder = StringBuilder()
		builder.append(timing.tag)
		builder.append(';')
		builder.append("dur=")
		builder.append(timing.time.toDouble(timeUnit))
		builder.append(';')
		builder.append("desc=")
		builder.append('"')
		builder.append(timing.desc)
		builder.append(" (${timeUnit.name})")
		builder.append('"')
		builder.toString()
	}.joinToString(", ") { it }
}