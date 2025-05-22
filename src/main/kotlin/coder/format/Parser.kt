package org.bread_experts_group.coder.format

import org.bread_experts_group.logging.ColoredLogger
import org.bread_experts_group.stream.FailQuickInputStream
import java.io.InputStream

abstract class Parser<I, O>(format: String, from: InputStream) : FailQuickInputStream(from) {
	protected val logger = ColoredLogger.newLogger("$format Parser")
	protected val parsers = mutableMapOf<I, (InputStream) -> O>()
	fun addParser(identifier: I, parser: (InputStream) -> O) {
		logger.fine { "Registering parser [$parser] for identifier [$identifier]" }
		if (parsers.containsKey(identifier))
			throw IllegalArgumentException("Parser for identifier \"$identifier\" already exists")
		parsers[identifier] = parser
	}

	abstract fun readParsed(): O
	fun readAllParsed() = buildList {
		try {
			while (true) add(readParsed())
		} catch (_: EndOfStream) {
		}
	}
}