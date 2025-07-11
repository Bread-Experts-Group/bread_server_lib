package org.bread_experts_group.coder.format.decode

import org.bread_experts_group.logging.ColoredHandler
import org.bread_experts_group.resource.LoggerResource
import org.bread_experts_group.stream.FailQuickInputStream
import java.io.InputStream
import java.util.logging.Logger

// TODO Lang
abstract class Decoder<S : InputStream, O>(
	private val format: String,
	protected val rawStream: S
) : AutoCloseable, Iterable<O> {
	protected open val fqIn = FailQuickInputStream(rawStream)
	protected val logger: Logger = ColoredHandler.newLogger("$format ${LoggerResource.get().getString("parser")}")

	protected abstract fun consumeNext(): O?
	protected fun refineNext(): O? {
		return try {
			consumeNext() ?: refineNext()
		} catch (_: FailQuickInputStream.EndOfStream) {
			null
		}
	}

	override fun close() {
		fqIn.close()
	}

	abstract var next: O?
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