package org.bread_experts_group.io

import kotlin.reflect.KFunction

class SelectiveIOLayout<O>(
	private val selectors: Map<KFunction<Boolean>, IOLayout<out O>>,
	private val writeAction: (BaseWritingIO, O) -> Unit,
	private vararg val layouts: IOLayout<out Any?>
) : IOLayout<O>() {
	val ioConsidered = layouts.filter { it.considerInIO }
	private val readAction by lazy {
		for ((selector, _) in selectors) {
			if ((selector.parameters.size - ioConsidered.size) !in 0..1) throw IOReadException(
				"Selector and IO parameters do not match " +
						"[${ioConsidered.size} / ${selector.parameters.size}]"
			)
		}
		val passedUpwards = layouts.filter { it.passedUpwards };
		select@{ from: BaseReadingIO ->
			val values = arrayOfNulls<Any>(ioConsidered.size)
			val passed = arrayOfNulls<Any>(passedUpwards.size)
			var valuesNext = 0
			var passedNext = 0
			layouts.forEach {
				val read = it.read(from)
				if (it.considerInIO) values[valuesNext++] = read
				if (it.passedUpwards) passed[passedNext++] = read
			}
			for ((selector, layout) in selectors) {
				if (
					if (selector.parameters.size > ioConsidered.size) selector.call(*values, from)
					else selector.call(*values)
				) {
					from.pass = passed
					return@select layout.read(from)
				}
			}
			throw IOReadException("No selectors matched ${values.contentToString()}")
		}
	}

	override fun read(from: BaseReadingIO): O = readAction(from)
	override fun write(to: BaseWritingIO, of: O) = writeAction(to, of)
	override fun padding(): SequentialIOLayout<O> = TODO("RHO")
	override fun passedUpwards(): SequentialIOLayout<O> = TODO("RHO")
	override fun withName(name: String): SequentialIOLayout<O> = TODO("RHO")
	override fun nullable(): IOLayout<O?> = TODO("RHO")
}