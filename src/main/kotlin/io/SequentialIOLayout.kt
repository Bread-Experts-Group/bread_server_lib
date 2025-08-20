package org.bread_experts_group.io

import kotlin.reflect.KFunction

class SequentialIOLayout<O>(
	private val constructor: KFunction<O>,
	private vararg val layouts: IOLayout<out Any?>
) : IOLayout<O>() {
	private val readAction by lazy {
		val passed = layouts.filter { it.passedUpwards }
		val ioConsidered = layouts.filter { it.considerInIO && !it.passedUpwards }
		val arguments = constructor.parameters.size - passed.size
		if ((arguments - ioConsidered.size) !in 0..1) throw IOReadException(
			"Constructor and considered IO parameters do not match " +
					"[${ioConsidered.size} / $arguments]"
		);
		{ from: BaseReadingIO ->
			val values = arrayOfNulls<Any>(ioConsidered.size)
			var next = 0
			for (layout in layouts) {
				if (layout.passedUpwards) continue
				val read = layout.read(from)
				if (layout.considerInIO) values[next++] = read
			}
			if (arguments > ioConsidered.size) constructor.call(*(from.pass ?: emptyArray()), *values, from)
			else constructor.call(*(from.pass ?: emptyArray()), *values)
		}
	}

	override fun read(from: BaseReadingIO): O = readAction(from)
	override fun write(to: BaseWritingIO, of: O) = TODO("LAMBDA")
	override fun padding(): SequentialIOLayout<O> = TODO("RHO")
	override fun passedUpwards(): IOLayout<O> = TODO("RHO")
}