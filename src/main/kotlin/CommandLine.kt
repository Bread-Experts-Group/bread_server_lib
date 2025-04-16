package org.bread_experts_group

import java.util.logging.Logger

fun stringToLong(str: String): Long =
	if (str.substring(0, 1) == "0x") str.substring(2).toLong(16)
	else if (str.substring(0, 1) == "0b") str.substring(2).toLong(2)
	else str.toLong()

fun stringToInt(str: String): Int = stringToLong(str).toInt()


fun stringToBoolean(str: String): Boolean = str.lowercase().let { it == "true" || it == "yes" || it == "1" }

open class Flag<T>(
	val flagName: String,
	val repeatable: Boolean = false,
	val default: T? = null,
	val conv: ((String) -> T) = {
		@Suppress("UNCHECKED_CAST")
		it as T
	}
)

typealias SingleArgs = Map<String, Any>
typealias MultipleArgs = Map<String, List<Any>>
typealias Args = Pair<SingleArgs, MultipleArgs>

fun readArgs(
	args: Array<String>,
	vararg flags: Flag<*>
) = readArgs(args, flags.toList())

private val logger = Logger.getLogger("Bread Server Library Argument Retrieval")
fun readArgs(
	args: Array<String>,
	flags: List<Flag<*>>
): Args {
	val singleArgs = mutableMapOf<String, Any>()
	val multipleArgs = mutableMapOf<String, MutableList<Any>>()
	args.forEach {
		logger.finer { "Parse argument \"$it\"" }
		if (it[0] != '-') throw IllegalArgumentException("Bad argument \"$it\", requires - before name")
		var equIndex = it.indexOf('=')
		val flag = flags.first { f -> f.flagName == it.substring(1, if (equIndex == -1) it.length else equIndex) }
		val value = if (equIndex == -1) "true" else it.substring(equIndex + 1)
		val typedValue = if (value.isNotBlank()) flag.conv(value) else flag.default
		logger.finer {
			"Conversion \"${typedValue.toString()}\" ${if (typedValue != null) "(${typedValue::class.simpleName})" else ""}"
		}
		if (typedValue != null) {
			if (flag.repeatable) {
				multipleArgs
					.getOrPut(flag.flagName) { mutableListOf() }
					.add(typedValue)
			} else {
				if (singleArgs.putIfAbsent(flag.flagName, typedValue) != null)
					throw IllegalArgumentException("Duplicate flag, \"${flag.flagName}\"")
			}
		}
	}
	flags.forEach {
		if (!it.repeatable && it.default != null && !singleArgs.contains(it.flagName)) {
			logger.finer { "Using default (\"${it.default}\" (${it.default::class.simpleName})) for flag \"${it.flagName}\"" }
			singleArgs.put(it.flagName, it.default)
		}
	}
	return singleArgs to multipleArgs
}