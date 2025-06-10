package org.bread_experts_group.logging

import org.bread_experts_group.logging.ansi_colorspace.ANSIColorSpace

class ANSIString(private var total: String = "") {
	private var length = 0
	private val ansiEscape = ''
	private var resetChar = "$ansiEscape[39m"

	fun append(c: Char) {
		this.total += c; length++
	}

	fun append(s: String) {
		this.total += s; length += s.length
	}

	fun color(n: ANSIColorSpace, init: ANSIString.() -> Unit): ANSIString = this.also {
		val last = resetChar
		resetChar = "$ansiEscape[${n.trailer()}"
		reset()
		init()
		resetChar = last
		reset()
	}

	var setResets = true
	fun reset() {
		if (setResets) this.total += resetChar
	}

	fun build() = this.total
	fun length() = this.length
}

fun ansi(init: ANSIString.() -> Unit): ANSIString = ANSIString().also { it.init() }