package org.bread_experts_group.command_line

import org.bread_experts_group.bslBuildDate
import org.bread_experts_group.bslVersion
import org.bread_experts_group.logging.ColoredHandler
import java.util.logging.Level
import kotlin.system.exitProcess

fun readArgs(
	args: Array<String>,
	projectName: String,
	projectUsage: String,
	vararg flags: Flag<*>
): ArgumentContainer = readArgs(args, flags.toList(), projectName, projectUsage)

private val logger = ColoredHandler.newLoggerResourced("program_argument_retrieval")

fun readArgs(
	args: Array<String>,
	flags: List<Flag<*>>,
	projectName: String,
	projectUsage: String
): ArgumentContainer {
	val argNames = mutableSetOf("help")
	flags.forEach {
		if (argNames.contains(it.flagName))
			throw ArgumentConstructionError("Duplicate argument parsing! [${it.flagName}]")
		argNames.add(it.flagName)
	}

	val singleArgs = mutableMapOf<String, Any>()
	val multipleArgs = mutableMapOf<String, MutableList<Any>>()
	val problems = mutableListOf<ArgumentParsingError>()
	var position = 0
	while (position < args.size) {
		val (name, parameter) = args[position++].let {
			logger.finer { "Parse argument \"$it\"" }
			if (it[0] != '-') {
				problems.add(ArgumentParsingError("Bad argument \"$it\", requires - before name"))
				continue
			}
			val afterDashes = it.substring(if (it[1] == '-') 2 else 1)
			val equSeparator = afterDashes.indexOf('=')
			if (equSeparator == -1) {
				val adjacent = args[position]
				if (adjacent[0] == '-') afterDashes.lowercase() to null
				else {
					position++
					afterDashes.lowercase() to adjacent
				}
			} else afterDashes.lowercase().take(equSeparator) to afterDashes.substring(equSeparator + 1)
		}
		if (name == "help") {
			val bslLocation = ColoredHandler::class.java.protectionDomain.codeSource.location.path
			logger.info("Bread Server Library information")
			logger.info("Location     [$bslLocation]")
			logger.info("Version      [${bslVersion()}]")
			logger.info("Compiled at  [${bslBuildDate()}]")
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

		val flag = flags.firstOrNull { f -> f.flagName == name }
		if (flag == null) {
			problems.add(ArgumentParsingError("Bad argument \"$name\", not a flag; see (-)-help"))
			continue
		}
		val value = parameter ?: "true"
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
		if (it.default != null) {
			if (!it.repeatable && !singleArgs.contains(it.flagName)) {
				logger.finer {
					"Using default (\"${it.default}\" (${it.default::class.simpleName})) for flag \"${it.flagName}\""
				}
				singleArgs.put(it.flagName, it.default)
			}
			if (it.repeatable && !multipleArgs.contains(it.flagName)) {
				logger.finer {
					"Using default (\"${it.default}\" (${it.default::class.simpleName})) for flag \"${it.flagName}\""
				}
				multipleArgs.put(it.flagName, mutableListOf(it.default))
			}
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