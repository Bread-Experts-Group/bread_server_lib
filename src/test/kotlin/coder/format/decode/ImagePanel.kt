package org.bread_experts_group.coder.format.decode

import java.awt.Dimension
import java.awt.Graphics
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.swing.JPanel
import javax.swing.SwingUtilities

class ImagePanel(val images: List<TimedBufferedImage>) : JPanel() {
	private var currentIndex = 0

	companion object {
		val animScheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
			Thread.ofVirtual().factory()
		)
	}

	init {
		if (images.size > 1) animScheduler.scheduleAtFixedRate({
			currentIndex = (currentIndex + 1) % images.size
			SwingUtilities.invokeLater { repaint() }
		}, 0, images[currentIndex].delay.inWholeMilliseconds, TimeUnit.MILLISECONDS)
	}

	override fun paintComponent(g: Graphics) {
		super.paintComponent(g)
		val img = images.getOrNull(currentIndex)?.image
		if (img != null) g.drawImage(img, 0, 0, this)
	}

	override fun getPreferredSize(): Dimension = Dimension(
		images[0].image.width,
		images[0].image.height
	)
}