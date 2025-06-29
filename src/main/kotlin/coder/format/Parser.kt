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
	protected val fqIn: InputStream = FailQuickInputStream(rawStream)
	protected val logger: Logger = ColoredHandler.newLogger("$format ${LoggerResource.get().getString("parser")}")
	protected val parsers: MutableMap<I, (S, O) -> O> = mutableMapOf<I, (S, O) -> O>()
	fun addParser(identifier: I, parser: (S, O) -> O) {
		logger.fine { "Registering parser [$parser] for identifier [$identifier]" }
		if (parsers.containsKey(identifier))
			throw IllegalArgumentException("Parser for identifier \"$identifier\" already exists")
		parsers[identifier] = parser
	}

	abstract var next: O?
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

	protected fun refineNext(): O? = try {
		refineBase(readBase())
	} catch (_: FailQuickInputStream.EndOfStream) {
		null
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