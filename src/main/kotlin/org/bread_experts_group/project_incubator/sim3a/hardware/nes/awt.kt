package org.bread_experts_group.project_incubator.sim3a.hardware.nes

import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.JPanel

class NesWindow(
	val framebuffer: IntArray
) : JPanel() {

	private val image = BufferedImage(256, 240, BufferedImage.TYPE_INT_ARGB)

	init {
		JFrame("NES").apply {
			defaultCloseOperation = JFrame.EXIT_ON_CLOSE
			contentPane = this@NesWindow
			isResizable = false
			setSize(256 * 3, 240 * 3)
			isVisible = true
		}
	}

	fun render() {
		image.setRGB(0, 0, 256, 240, framebuffer, 0, 256)
		repaint()
	}

	override fun paint(g: Graphics) {
		super.paint(g)
		val g2 = g as Graphics2D
		g2.scale(3.0, 3.0)
		g2.drawImage(image, 0, 0, null)
	}
}