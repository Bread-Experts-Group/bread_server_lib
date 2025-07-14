package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.LazyMutable
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.EOFException
import java.nio.channels.ReadableByteChannel
import java.util.function.Predicate
import java.util.logging.Logger

private typealias SubParserB<O, C> = (C, O, CodingCompoundThrowable, Array<out Any>, Array<out Any>) -> O
private typealias ParserGroupB<O, C> = Pair<SubParserB<O, C>, Array<out Any>>

abstract class ByteParser<T, O, C : ReadableByteChannel>(
	private val format: String
) : AutoCloseable, Iterable<CodingPartialResult<O>> where O : Tagged<T>, O : Writable {
	protected lateinit var channel: C
	protected val logger: Logger = ColoredHandler.newLogger("$format ${LoggerResource.get().getString("parser")}")
	protected val parsers = mutableMapOf<T, ParserGroupB<O, C>>()
	protected val predicateParsers = mutableMapOf<Predicate<O>, ParserGroupB<O, C>>()
	private var throwOnUnknown = false
	fun throwOnUnknown() = this.also { it.throwOnUnknown = true }
	protected open fun isUnknown(of: O) = true

	open fun addParser(identifier: T, parser: (C, O, CodingCompoundThrowable) -> O) {
		addParserParameterized(identifier, { stream, o, c, _, _ -> parser(stream, o, c) })
	}

	open fun addPredicateParser(predicate: Predicate<O>, parser: (C, O, CodingCompoundThrowable) -> O) {
		addPredicateParserParameterized(predicate, { stream, o, c, _, _ -> parser(stream, o, c) })
	}

	open fun addPredicateParserParameterized(
		predicate: Predicate<O>, parser: (C, O, CodingCompoundThrowable, Array<out Any>, Array<out Any>) -> O,
		vararg additional: Any
	) {
		logger.fine { "Registering parser [$parser] for predicate [$predicate]" }
		if (predicateParsers.containsKey(predicate))
			throw IllegalArgumentException("Parser for predicate [$predicate] already exists")
		predicateParsers[predicate] = parser to additional
	}

	open fun addParserParameterized(
		identifier: T, parser: (C, O, CodingCompoundThrowable, Array<out Any>, Array<out Any>) -> O,
		vararg additional: Any
	) {
		logger.fine { "Registering parser [$parser] for identifier [$identifier]" }
		if (parsers.containsKey(identifier))
			throw IllegalArgumentException("Parser for identifier [$identifier] already exists")
		parsers[identifier] = parser to additional
	}

	var next: CodingPartialResult<O>? by LazyMutable {
		refineNext(CodingCompoundThrowable())?.let {
			LazyPartialResult(it.first, it.second)
		}
	}

	protected abstract fun responsibleChannel(of: O): C
	protected abstract fun readBase(compound: CodingCompoundThrowable): O?

	protected open fun fallbackBase(
		compound: CodingCompoundThrowable, of: O,
		vararg parameter: Any
	) = of

	protected open fun refineBase(
		compound: CodingCompoundThrowable, of: O,
		vararg parameters: Any
	): Pair<O, CodingException?> {
		val (parser, additionalParam) = (this.parsers[of.tag]
			?: this.predicateParsers.firstNotNullOfOrNull { (predicate, group) ->
				if (predicate.test(of)) group
				else null
			})
			?: if (throwOnUnknown && isUnknown(of)) throw MissingParserException(of, parameters)
			else return fallbackBase(
				compound, of,
				parameters
			) to compound.build()?.let {
				FallbackException("Compounded failure [${compound.thrown.size}]", it)
			}
		return try {
			parser(
				responsibleChannel(of), of, compound,
				additionalParam, parameters
			).also {
				this.logger.fine {
					"Parsed chunk [${of.javaClass.canonicalName}] into [${it.javaClass.canonicalName}] from [$parser]"
				}
			}
		} catch (e: Exception) {
			compound.addThrown(RefinementException("Failure during parsing refinement", e))
			of
		} to compound.build()?.let {
			RefinementException("Compounded failure [${compound.thrown.size}]", it)
		}
	}

	protected fun refineNext(compound: CodingCompoundThrowable): Pair<O, CodingException?>? {
		return try {
			refineBase(compound, readBase(compound) ?: return null)
		} catch (_: EOFException) {
			null
		}
	}

	override fun close() {
		channel.close()
	}

	private val standardIterator = object : Iterator<CodingPartialResult<O>> {
		override fun next(): CodingPartialResult<O> {
			val current = next!!
			next = refineNext(CodingCompoundThrowable())?.let {
				LazyPartialResult(it.first, it.second)
			}
			return current
		}

		override fun hasNext(): Boolean = next != null
	}

	override fun iterator(): Iterator<CodingPartialResult<O>> = standardIterator
	override fun toString(): String = "${this::class.simpleName}[\"$format\"]"

	fun setInput(from: C): ByteParser<T, O, C> {
		channel = from
		inputInit()
		next = refineNext(CodingCompoundThrowable())?.let {
			LazyPartialResult(it.first, it.second)
		}
		return this
	}

	open fun inputInit() {}
}