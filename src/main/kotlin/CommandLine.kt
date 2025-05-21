package org.bread_experts_group

import org.bread_experts_group.logging.ColoredLogger
import kotlin.system.exitProcess

fun stringToLong(str: String): Long =
	if (str.substring(0, 1) == "0x") str.substring(2).toLong(16)
	else if (str.substring(0, 1) == "0b") str.substring(2).toLong(2)
	else str.toLong()

fun stringToInt(str: String): Int = stringToLong(str).toInt()
fun stringToBoolean(str: String): Boolean = str.lowercase().let { it == "true" || it == "yes" || it == "1" }

class ArgumentConstructionError(msg: String) : Error(msg)

open class Flag<T>(
	flagName: String,
	val flagDescription: String,
	val repeatable: Boolean = false,
	val required: Int = 0,
	val default: T? = null,
	val conv: ((String) -> T) = {
		@Suppress("UNCHECKED_CAST")
		it as T
	}
) {
	val flagName = flagName.lowercase().replace('-', '_')

	init {
		if (required > 1 && !repeatable)
			throw ArgumentConstructionError("[$flagName] requires $required specifications, but isn't repeatable")
	}
}

typealias SingleArgs = Map<String, Any>
typealias MultipleArgs = Map<String, List<Any>>
typealias Args = Pair<SingleArgs, MultipleArgs>

fun readArgs(
	args: Array<String>,
	projectName: String,
	projectUsage: String,
	vararg flags: Flag<*>
) = readArgs(args, flags.toList(), projectName, projectUsage)

private val logger = ColoredLogger.newLogger("Program Argument Retrieval")

class RequiredArgumentsMissingException(flag: Flag<*>, count: Int) : RuntimeException(buildString {
	append("Missing flags for [${flag.flagName}]\n")
	append("\t${flag.flagDescription.replace("\n", "\n\t\t ")}\n")
	append("* Required ${flag.required} time${if (flag.required > 1) 's' else ""}, got $count")
})

fun readArgs(
	args: Array<String>,
	flags: List<Flag<*>>,
	projectName: String,
	projectUsage: String
): Args {
	run {
		val argNames = mutableSetOf("help")
		flags.forEach {
			if (argNames.contains(it.flagName))
				throw ArgumentConstructionError("Duplicate argument parsing! [${it.flagName}]")
			argNames.add(it.flagName)
		}
	}
	if (args.any { it.substringAfter('-') == "help" }) {
		val bslLocation = ColoredLogger::class.java.protectionDomain.codeSource.location.path
		val buildInfo = ColoredLogger::class.java.classLoader.loadClass("org.bread_experts_group.BuildInfo")
		logger.info("Bread Server Library information")
		logger.info("Location     [$bslLocation]")
		logger.info("Version      [${buildInfo.getField("VERSION").get(null) as String}]")
		logger.info("Compiled at  [${buildInfo.getField("COMPILE_DATE").get(null) as String}]")
		logger.info("Project information")
		logger.info("Project      [$projectName]")
		logger.info("Usage        [$projectUsage]")
		logger.info("Flag information\n" + buildString {
			var longestFlagName = 0
			var longestFlagDescription = 0
			flags.forEach {
				if (it.flagName.length > longestFlagName)
					longestFlagName = it.flagName.length
				if (it.flagDescription.length > longestFlagDescription)
					longestFlagDescription = it.flagDescription.length
			}
			flags.forEach {
				append("\t-${it.flagName.padEnd(longestFlagName)}\n")
				append("\t\t${it.flagDescription.replace("\n", "\n\t\t ").padEnd(longestFlagDescription)}\n")
				if (it.default != null) append("\t\tDefault [${it.default}]\n")
				if (it.required > 0) append("\t\tRequired ${it.required} time${if (it.required > 1) 's' else ""}")
			}
		})
		exitProcess(3319)
	}

	val singleArgs = mutableMapOf<String, Any>()
	val multipleArgs = mutableMapOf<String, MutableList<Any>>()
	args.forEach {
		logger.finer { "Parse argument \"$it\"" }
		if (it[0] != '-') throw IllegalArgumentException("Bad argument \"$it\", requires - before name")
		val equIndex = it.indexOf('=')
		val flag = flags.first { f -> f.flagName == it.substring(1, if (equIndex == -1) it.length else equIndex) }
		val value = if (equIndex == -1) "true" else it.substring(equIndex + 1)
		val typedValue = if (value.isNotBlank()) flag.conv(value) else flag.default
		logger.finer {
			"Conversion \"${typedValue.toString()}\" " +
					if (typedValue != null) "(${typedValue::class.simpleName})"
					else ""
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
			logger.finer {
				"Using default (\"${it.default}\" (${it.default::class.simpleName})) for flag \"${it.flagName}\""
			}
			singleArgs.put(it.flagName, it.default)
		}
		if (it.required == 1 && !singleArgs.containsKey(it.flagName))
			throw RequiredArgumentsMissingException(it, 0)
		if (it.required > 1)
			if (!multipleArgs.containsKey(it.flagName))
				throw RequiredArgumentsMissingException(it, 0)
			else multipleArgs.getValue(it.flagName).let { a ->
				if (a.size < it.required) throw RequiredArgumentsMissingException(
					it,
					a.size
				)
			}
	}
	return singleArgs to multipleArgs
}