package org.bread_experts_group.command_line

import org.bread_experts_group.logging.ColoredHandler
import java.util.logging.Level
import kotlin.system.exitProcess

fun readArgs(
	args: Array<String>,
	projectName: String,
	projectUsage: String,
	vararg flags: Flag<*>
): ArgumentContainer = readArgs(args, flags.toList(), projectName, projectUsage)

private val logger = ColoredHandler.newLogger("Program Argument Retrieval")

fun readArgs(
	args: Array<String>,
	flags: List<Flag<*>>,
	projectName: String,
	projectUsage: String
): ArgumentContainer {
	run {
		val argNames = mutableSetOf("help")
		flags.forEach {
			if (argNames.contains(it.flagName))
				throw ArgumentConstructionError("Duplicate argument parsing! [${it.flagName}]")
			argNames.add(it.flagName)
		}
	}
	if (args.any { it.substringAfter('-') == "help" }) {
		val bslLocation = ColoredHandler::class.java.protectionDomain.codeSource.location.path
		val buildInfo = ColoredHandler::class.java.classLoader.loadClass("org.bread_experts_group.BuildInfo")
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
				if (it.required > 0) append("\t\tRequired ${it.required} time${if (it.required > 1) 's' else ""}\n")
			}
		})
		exitProcess(3319)
	}

	val singleArgs = mutableMapOf<String, Any>()
	val multipleArgs = mutableMapOf<String, MutableList<Any>>()
	val problems = mutableListOf<ArgumentParsingError>()
	for (arg in args) {
		logger.finer { "Parse argument \"$arg\"" }
		if (arg[0] != '-') {
			problems.add(ArgumentParsingError("Bad argument \"$arg\", requires - before name"))
			continue
		}
		val equIndex = arg.indexOf('=')
		val flag = flags.firstOrNull { f ->
			f.flagName == arg.substring(1, if (equIndex == -1) arg.length else equIndex)
		}
		if (flag == null) {
			problems.add(ArgumentParsingError("Bad argument \"$arg\", not a flag; see -help"))
			continue
		}
		val value = if (equIndex == -1) "true" else arg.substring(equIndex + 1)
		val typedValue = try {
			if (value.isNotBlank()) flag.conv(value) else flag.default
		} catch (e: Throwable) {
			problems.add(ArgumentParsingError("Error while converting argument", flag, e))
			continue
		}
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
				if (singleArgs.putIfAbsent(flag.flagName, typedValue) != null) problems.add(
					ArgumentParsingError("Duplicate flag", flag)
				)
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
		if (it.required != 0 && !(singleArgs.containsKey(it.flagName) || multipleArgs.containsKey(it.flagName)))
			problems.add(RequiredArgumentsMissingException(it, 0))
		else if (it.required > 1)
			multipleArgs.getValue(it.flagName).let { a ->
				if (a.size < it.required) problems.add(RequiredArgumentsMissingException(it, a.size))
			}
	}
	if (problems.isNotEmpty()) {
		problems.forEachIndexed { i, problem -> logger.log(Level.SEVERE, problem) { "Argument problem [$i]" } }
		exitProcess(3122)
	}
	return ArgumentContainer(singleArgs + multipleArgs)
}