package org.bread_experts_group.coder.format

import org.bread_experts_group.coder.DecodingException
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.InputStream
import java.util.function.Predicate
import java.util.logging.Logger

private typealias ParserGroup<S, O> = Pair<(S, O, Array<out Any>, Array<out Any>) -> O, Array<out Any>>

abstract class Parser<T, O, S : InputStream>(
	private val format: String,
	protected val rawStream: S
) : AutoCloseable, Iterable<O> where O : Tagged<T>, O : Writable {

	protected open val fqIn = FailQuickInputStream(rawStream)
	protected val logger: Logger = ColoredHandler.newLogger("$format ${LoggerResource.get().getString("parser")}")
	protected val parsers = mutableMapOf<T, ParserGroup<S, O>>()
	protected val predicateParsers = mutableMapOf<Predicate<T>, ParserGroup<S, O>>()
	private var throwOnUnknown = false
	fun throwOnUnknown() = this.also { it.throwOnUnknown = true }

	open fun addParser(identifier: T, parser: (S, O) -> O) {
		addParserParameterized(identifier, { stream, o, _, _ -> parser(stream, o) })
	}

	open fun addPredicateParser(predicate: Predicate<T>, parser: (S, O) -> O) {
		addPredicateParserParameterized(predicate, { stream, o, _, _ -> parser(stream, o) })
	}

	open fun addPredicateParserParameterized(
		predicate: Predicate<T>, parser: (S, O, Array<out Any>, Array<out Any>) -> O,
		vararg additional: Any
	) {
		logger.fine { "Registering parser [$parser] for predicate [$predicate]" }
		if (predicateParsers.containsKey(predicate))
			throw IllegalArgumentException("Parser for predicate [$predicate] already exists")
		predicateParsers[predicate] = parser to additional
	}

	open fun addParserParameterized(
		identifier: T, parser: (S, O, Array<out Any>, Array<out Any>) -> O,
		vararg additional: Any
	) {
		logger.fine { "Registering parser [$parser] for identifier [$identifier]" }
		if (parsers.containsKey(identifier))
			throw IllegalArgumentException("Parser for identifier [$identifier] already exists")
		parsers[identifier] = parser to additional
	}

	abstract var next: O?
	protected abstract fun responsibleStream(of: O): S
	protected abstract fun readBase(): O?
	protected open fun refineBase(of: O, vararg parameters: Any): O {
		val (parser, additionalParam) = (this.parsers[of.tag]
			?: this.predicateParsers.firstNotNullOfOrNull { (predicate, group) ->
				if (predicate.test(of.tag)) group
				else null
			})
			?: if (throwOnUnknown) throw DecodingException("No parser for [$of] / ${parameters.toList()}")
			else return of
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

	override fun toString(): String = "${this::class.simpleName}[\"$format\", #${rawStream.available()}]"
}