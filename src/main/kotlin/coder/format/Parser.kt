package org.bread_experts_group.coder.format

import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.InputStream
import java.util.logging.Logger

abstract class Parser<I, O, S : InputStream>(
	format: String,
	protected val rawStream: S
) : AutoCloseable, Iterable<O> where O : Tagged<I>, O : Writable {
	protected open val fqIn = FailQuickInputStream(rawStream)
	protected val logger: Logger = ColoredHandler.newLogger("$format ${LoggerResource.get().getString("parser")}")
	protected val parsers = mutableMapOf<I, Pair<(S, O, Array<out Any>, Array<out Any>) -> O, Array<out Any>>>()

	open fun addParser(identifier: I, parser: (S, O) -> O) {
		addParserParameterized(identifier, { stream, o, _, _ -> parser(stream, o) })
	}

	open fun addParserParameterized(
		identifier: I, parser: (S, O, Array<out Any>, Array<out Any>) -> O,
		vararg additional: Any
	) {
		logger.fine { "Registering parser [$parser] for identifier [$identifier]" }
		if (parsers.containsKey(identifier))
			throw IllegalArgumentException("Parser for identifier \"$identifier\" already exists")
		parsers[identifier] = parser to additional
	}

	abstract var next: O?
	protected abstract fun responsibleStream(of: O): S
	protected abstract fun readBase(): O?
	protected open fun refineBase(of: O, vararg parameters: Any): O {
		val (parser, additionalParam) = this.parsers[of.tag] ?: return of
		return parser(responsibleStream(of), of, additionalParam, parameters).also {
			this.logger.fine {
				"Parsed chunk [${of.javaClass.canonicalName}] into [${it.javaClass.canonicalName}] from [$parser]"
			}
		}
	}

	protected fun refineNext(): O? {
		return try {
			refineBase(readBase() ?: return refineNext())
		} catch (_: FailQuickInputStream.EndOfStream) {
			null
		}
	}

	override fun close() {
		fqIn.close()
	}

	override fun iterator(): Iterator<O> = object : Iterator<O> {
		override fun next(): O {
			val current = next!!
			next = refineNext()
			return current
		}

		override fun hasNext(): Boolean = next != null
	}
}