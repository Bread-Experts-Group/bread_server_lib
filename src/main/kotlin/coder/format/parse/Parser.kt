package org.bread_experts_group.coder.format.parse

import org.bread_experts_group.coder.CodingException
import org.bread_experts_group.coder.CompoundThrowable
import org.bread_experts_group.coder.LazyMutable
import org.bread_experts_group.coder.LazyPartialResult
import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.FailQuickInputStream
import org.bread_experts_group.stream.Tagged
import org.bread_experts_group.stream.Writable
import java.io.InputStream
import java.util.function.Predicate
import java.util.logging.Logger
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

typealias CodingCompoundThrowable = CompoundThrowable<CodingException>
typealias CodingPartialResult<O> = LazyPartialResult<O, CodingException>
private typealias SubParser<O> = (InputStream, O, CodingCompoundThrowable, Array<out Any>, Array<out Any>) -> O
private typealias ParserGroup<O> = Pair<SubParser<O>, Array<out Any>>

abstract class Parser<T, O, S : InputStream>(
	private val format: String,
	private val streamClass: KClass<S>
) : AutoCloseable, Iterable<CodingPartialResult<O>> where O : Tagged<T>, O : Writable {

	protected lateinit var fqIn: FailQuickInputStream<S>
	protected val logger: Logger = ColoredHandler.newLogger("$format ${LoggerResource.get().getString("parser")}")
	protected val parsers = mutableMapOf<T, ParserGroup<O>>()
	protected val predicateParsers = mutableMapOf<Predicate<O>, ParserGroup<O>>()
	private var throwOnUnknown = false
	fun throwOnUnknown() = this.also { it.throwOnUnknown = true }

	open fun addParser(identifier: T, parser: (InputStream, O, CodingCompoundThrowable) -> O) {
		addParserParameterized(identifier, { stream, o, c, _, _ -> parser(stream, o, c) })
	}

	open fun addPredicateParser(predicate: Predicate<O>, parser: (InputStream, O, CodingCompoundThrowable) -> O) {
		addPredicateParserParameterized(predicate, { stream, o, c, _, _ -> parser(stream, o, c) })
	}

	open fun addPredicateParserParameterized(
		predicate: Predicate<O>, parser: (InputStream, O, CodingCompoundThrowable, Array<out Any>, Array<out Any>) -> O,
		vararg additional: Any
	) {
		logger.fine { "Registering parser [$parser] for predicate [$predicate]" }
		if (predicateParsers.containsKey(predicate))
			throw IllegalArgumentException("Parser for predicate [$predicate] already exists")
		predicateParsers[predicate] = parser to additional
	}

	open fun addParserParameterized(
		identifier: T, parser: (InputStream, O, CodingCompoundThrowable, Array<out Any>, Array<out Any>) -> O,
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

	protected abstract fun responsibleStream(of: O): S
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
			?: if (throwOnUnknown) throw MissingParserException(of, parameters)
			else return fallbackBase(
				compound, of,
				parameters
			) to compound.build()?.let {
				FallbackException("Compounded failure [${compound.thrown.size}]", it)
			}
		return try {
			parser(
				FailQuickInputStream(responsibleStream(of)), of, compound,
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
			refineBase(compound, readBase(compound) ?: return refineNext(compound))
		} catch (_: FailQuickInputStream.EndOfStream) {
			null
		}
	}

	override fun close() {
		fqIn.close()
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

	fun setInput(from: InputStream): Parser<T, O, S> {
		if (streamClass.isSuperclassOf(from::class))
			@Suppress("UNCHECKED_CAST") return setInputFq(FailQuickInputStream(from as S))
		if (from is FailQuickInputStream<*> && streamClass.isInstance(from.from))
			@Suppress("UNCHECKED_CAST") return setInputFq(from as FailQuickInputStream<S>)
		throw IllegalArgumentException("[$from] is not [$streamClass]")
	}

	fun setInputFq(from: FailQuickInputStream<S>): Parser<T, O, S> {
		fqIn = from
		inputInit()
		return this
	}

	open fun inputInit() {}
}