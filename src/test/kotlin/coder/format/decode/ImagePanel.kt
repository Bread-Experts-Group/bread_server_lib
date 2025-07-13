package org.bread_experts_group.coder.format.decode

import java.awt.Dimension
import java.awt.Graphics
import javax.swing.JPanel
import javax.swing.Timer

class ImagePanel(val images: List<TimedBufferedImage>) : JPanel() {
	private var currentIndex = 0
	private var timer: Timer? = null

	init {
		startAnimation()
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		val img = images.getOrNull(currentIndex)?.image
		if (img != null) {
			g.drawImage(img, 0, 0, this)
		}
	}

	override fun getPreferredSize(): Dimension =
		Dimension(images[0].image.width, images[0].image.height)

	private fun startAnimation() {
		if (images.isEmpty()) return

		fun scheduleNextFrame() {
			val current = images[currentIndex]
			timer?.stop()
			timer = Timer(current.delay.inWholeMilliseconds.coerceAtMost(Int.MAX_VALUE.toLong()).toInt()) {
				currentIndex = (currentIndex + 1) % images.size
				repaint()
				scheduleNextFrame()
			}
			timer?.isRepeats = false
			timer?.start()
		}

		scheduleNextFrame()
	}
}