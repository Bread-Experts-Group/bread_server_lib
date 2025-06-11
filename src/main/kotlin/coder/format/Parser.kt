package org.bread_experts_group.coder.format

import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.InputStream

abstract class Parser<I, O, S : InputStream>(
	format: String,
	protected val from: S
) : FailQuickInputStream(from) where O : Tagged<I>, O : Writable {
	protected val logger = ColoredHandler.newLogger("$format Parser")
	protected val parsers = mutableMapOf<I, (S, O) -> O>()
	fun addParser(identifier: I, parser: (S, O) -> O) {
		logger.fine { "Registering parser [$parser] for identifier [$identifier]" }
		if (parsers.containsKey(identifier))
			throw IllegalArgumentException("Parser for identifier \"$identifier\" already exists")
		parsers[identifier] = parser
	}

	protected abstract fun responsibleStream(of: O): S
	protected abstract fun readBase(): O
	protected open fun refineBase(of: O): O {
		val parser = this.parsers[of.tag]
		return parser?.invoke(responsibleStream(of), of)?.also {
			this.logger.fine {
				"Parsed chunk [${of.javaClass.canonicalName}] into [${it.javaClass.canonicalName}] from [$parser]"
			}
		} ?: of
	}

	fun readParsed(): O = refineBase(readBase())

	fun readAllParsed() = buildList {
		try {
			while (true) add(readParsed())
		} catch (_: EndOfStream) {
		}
	}
}