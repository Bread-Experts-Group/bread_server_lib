package org.bread_experts_group.computer.ia32

import org.bread_experts_group.computer.BinaryUtil.shr
import org.bread_experts_group.computer.Computer
import org.bread_experts_group.computer.ia32.bios.StandardBIOS
import org.bread_experts_group.computer.ia32.bios.TeletypeOutput
import org.bread_experts_group.getResource
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import kotlin.io.path.inputStream


class ProcessorVisualMonitor(computer: Computer) : JFrame() {
	private val teletype = (computer.bios as StandardBIOS).teletype

	init {
		title = "Processor Visual Monitor [BIOS Text Mode]"
		defaultCloseOperation = EXIT_ON_CLOSE
		isResizable = false

		val vgaFont = Font.createFont(
			Font.TRUETYPE_FONT,
			getResource("/px437_ibm_vga_9_14.ttf").inputStream()
		).deriveFont(17f)
		val dummy = JLabel("W")
		dummy.font = vgaFont
		val metrics = dummy.getFontMetrics(vgaFont)
		val cellWidth = metrics.charWidth('W')
		val cellHeight = metrics.height
		val ascent = metrics.ascent

		contentPane = object : JPanel() {
			override fun paintComponent(g: Graphics) {
				super.paintComponent(g)
				g.color = Color.BLACK
				g.fillRect(0, 0, width, height)
				g.font = font
				g.font = vgaFont
				for (position in 0u..<teletype.characters) {
					val data = computer.getMemoryAt16(TeletypeOutput.COLOR_ADDR + (position * 2u))
					val x = (position.mod(teletype.cols)).toInt()
					val y = (position.floorDiv(teletype.cols)).toInt()
					g.color = Color(
						((data and 0b1000000u shr 6) * 127u).toInt(),
						((data and 0b0100000u shr 5) * 127u).toInt(),
						((data and 0b0010000u shr 4) * 127u).toInt(),
					)
					g.drawRect(
						x * cellWidth, y * cellHeight + ascent,
						cellWidth, cellHeight + ascent
					)
					val character = Char(data shr 8)
					val intense = data and 0b1000u > 0u
					g.color = Color(
						((data and 0b0100u shr 2) * 127u).toInt() * (if (intense) 2 else 1),
						((data and 0b0010u shr 1) * 127u).toInt() * (if (intense) 2 else 1),
						((data and 0b0001u) * 127u).toInt() * (if (intense) 2 else 1),
					)
					g.drawString(
						character.toString(),
						x * cellWidth,
						y * cellHeight + ascent
					)
				}
			}
		}.apply {
			preferredSize = Dimension(teletype.cols.toInt() * cellWidth, teletype.rows.toInt() * cellHeight)
			background = Color.BLACK
		}

		pack()
		setLocationRelativeTo(null)
		isVisible = true
	}
}