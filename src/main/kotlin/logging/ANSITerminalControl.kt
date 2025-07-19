package org.bread_experts_group.logging

import org.bread_experts_group.logging.ansi_colorspace.ANSIColorSpace

class ANSIString(val total: StringBuilder = StringBuilder()) {
	private var reset = "${ansiEscape}39m"
	var length: Int = 0

	fun append(c: Char) {
		total.append(c)
		length += 1
	}

	fun append(s: String) {
		total.append(s)
		length += s.length
	}

	override fun toString(): String = total.toString()

	fun color(n: ANSIColorSpace, init: ANSIString.() -> Unit): ANSIString = this.also {
		val last = reset
		reset = n.trailer
		reset()
		init()
		reset = last
		reset()
	}

	var setResets: Boolean = true
	private fun reset() {
		if (setResets) this.total.append(reset)
	}
}

const val ansiEscape = "["
fun ansi(init: ANSIString.() -> Unit): ANSIString = ANSIString().also { it.init() }