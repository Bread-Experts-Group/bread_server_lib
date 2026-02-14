package org.bread_experts_group.generic.io

import kotlin.reflect.KFunction

open class SequentialIOLayout<O>(
	private val constructor: KFunction<O>,
	private vararg val layouts: IOLayout<out Any?>
) : IOLayout<O>() {
	protected open val readAction by lazy {
		val passed = layouts.filter { it.passedUpwards }
		val ioConsidered = layouts.filter { it.considerInIO && !it.passedUpwards }
		val arguments = constructor.parameters.size - passed.size
		if ((arguments - ioConsidered.size) !in 0..1) throw IOReadException(
			"Constructor and considered IO parameters do not match " +
					"[${ioConsidered.size} / $arguments]"
		);
		{ from: BaseReadingIO ->
			this.name?.let { from.enter(it) }
			val values = arrayOfNulls<Any>(ioConsidered.size)
			var next = 0
			for (layout in layouts) {
				if (layout.passedUpwards) continue
				val read = layout.read(from)
				if (layout.considerInIO) values[next++] = read
			}
			val v = if (arguments > ioConsidered.size) constructor.call(*(from.pass ?: emptyArray()), *values, from)
			else constructor.call(*(from.pass ?: emptyArray()), *values)
			this.name?.let { from.exit() }
			v
		}
	}

	override fun read(from: BaseReadingIO): O = readAction(from)
	override fun write(to: BaseWritingIO, of: O) = TODO("LAMBDA")
	override fun padding(): SequentialIOLayout<O> = TODO("RHO")
	override fun passedUpwards(): SequentialIOLayout<O> = TODO("RHO")
	override fun withName(name: String): SequentialIOLayout<O> {
		val layout = SequentialIOLayout<O>(this.constructor, *this.layouts)
		layout.name = name
		return layout
	}

	fun sequence(n: Int): SequencedIOLayout<O> = SequencedIOLayout(n, this)
	override fun nullable(): SequentialIOLayout<O?> = object : SequentialIOLayout<O?>(
		this@SequentialIOLayout.constructor,
		*this@SequentialIOLayout.layouts
	) {
		init {
			this.name = this@SequentialIOLayout.name
		}

		override val readAction: (BaseReadingIO) -> O? by lazy {
			{ from: BaseReadingIO ->
				try {
					super.readAction(from)
				} catch (e: NoSuchElementException) {
					null
				}
			}
		}
	}
}