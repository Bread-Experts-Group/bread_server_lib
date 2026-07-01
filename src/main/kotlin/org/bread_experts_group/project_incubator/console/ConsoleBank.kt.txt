package org.bread_experts_group.project_incubator.console

import org.bread_experts_group.generic.protocol.vt100d.SET_ATTRIBUTE
import org.bread_experts_group.generic.protocol.vt100d.VT100DGraphicsAttributes
import java.util.*

class ConsoleBank(private val messageQueue: Queue<ConsoleMessage>) {
	private val panel: MutableList<String> = Collections.synchronizedList(mutableListOf<String>())
	var line: Int = 0

	fun write(str: String) {
		var storage = panel.removeLastOrNull() ?: ""
		for (char in str) {
			storage += char
			if (char == '\n') {
				panel.addLast(storage)
				storage = ""
			}
		}
		panel.addLast(storage)
		messageQueue.add(ConsoleMessage.Refresh())
	}

	context(window: ConsoleMessage.WindowSize, console: Console)
	fun render(): String {
		var rendered = ""
		var i = line
		while (((i + 3) - line) <= window.y && i < panel.size) {
			val rawLine = panel[i++]
			var newLine = ""
			var containsLF = false
			for (char in rawLine) when {
				console.controlStatus -> when {
					console.controlSpaceStatus && char == ' ' -> newLine += console.controlSpace
					char == '\t' -> newLine += if (console.controlTabStatus) console.controlTab else "    "
					console.controlCrStatus && char == '\r' -> newLine += console.controlCr
					char == '\n' -> {
						if (console.controlLfStatus) newLine += console.controlLf
						containsLF = true
					}

					else -> newLine += char
				}

				char == '\t' -> newLine += "    "
				char == '\n' -> containsLF = true
				char != '\r' -> newLine += char
			}
			val etxLength = if (i >= panel.size) newLine.length + 1 else newLine.length
			newLine = if (etxLength > window.x) "${newLine.take(window.x - 1)}${
				SET_ATTRIBUTE(
					VT100DGraphicsAttributes.FG_YELLOW
				)
			}>${SET_ATTRIBUTE(VT100DGraphicsAttributes.FG_DEFAULT)}"
			else if (i >= panel.size && etxLength <= window.x) newLine + console.controlEtx
			else newLine.take(window.x)
			rendered += newLine + (if (containsLF) '\n' else "")
			if (i >= panel.size) break
		}
		return rendered
	}

	fun clear() {
		panel.clear()
	}
}