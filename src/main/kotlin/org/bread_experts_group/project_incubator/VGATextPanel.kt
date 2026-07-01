package org.bread_experts_group.project_incubator

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout
import javax.swing.JPanel

class VgaTextPanel(
	private val memory: MemorySegment
) : JPanel() {

	init {
		preferredSize = Dimension(80 * 9, 25 * 16)
		background = Color.BLACK
		foreground = Color.LIGHT_GRAY
		font = Font(Font.MONOSPACED, Font.PLAIN, 16)
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)

		val fm = g.fontMetrics
		val ascent = fm.ascent
		val cellWidth = fm.charWidth('W')
		val cellHeight = fm.height

		for (y in 0 until 25) {
			for (x in 0 until 80) {

				val offset = 0xB8000L + ((y * 80 + x) * 2L)

				val ch = memory.get(ValueLayout.JAVA_BYTE, offset).toInt() and 0xFF
				val attr = memory.get(ValueLayout.JAVA_BYTE, offset + 1).toInt() and 0xFF

				val fg = attr and 0x0F
				val bg = (attr ushr 4) and 0x0F

				g.color = vgaColor(bg)
				g.fillRect(
					x * cellWidth,
					y * cellHeight,
					cellWidth,
					cellHeight
				)

				g.color = vgaColor(fg)

				val c =
					if (ch in 32..126)
						ch.toChar()
					else
						' '

				g.drawString(
					c.toString(),
					x * cellWidth,
					y * cellHeight + ascent
				)
			}
		}
	}

	private fun vgaColor(i: Int): Color =
		when (i) {
			0 -> Color.BLACK
			1 -> Color(0, 0, 170)
			2 -> Color(0, 170, 0)
			3 -> Color(0, 170, 170)
			4 -> Color(170, 0, 0)
			5 -> Color(170, 0, 170)
			6 -> Color(170, 85, 0)
			7 -> Color(170, 170, 170)
			8 -> Color(85, 85, 85)
			9 -> Color(85, 85, 255)
			10 -> Color(85, 255, 85)
			11 -> Color(85, 255, 255)
			12 -> Color(255, 85, 85)
			13 -> Color(255, 85, 255)
			14 -> Color(255, 255, 85)
			else -> Color.WHITE
		}
}