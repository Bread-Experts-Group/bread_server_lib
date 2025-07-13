package org.bread_experts_group.coder.format.decode

import org.bread_experts_group.coder.format.decode.png.PNGImageDecoder
import org.bread_experts_group.coder.format.parse.CodingPartialResult
import org.bread_experts_group.coder.format.parse.png.PNGParser
import org.bread_experts_group.coder.format.parse.png.chunk.*
import org.bread_experts_group.dumpLog
import org.bread_experts_group.logging.ColoredHandler
import org.junit.jupiter.api.Test
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.io.BufferedInputStream
import java.io.File
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JScrollPane
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.time.Duration

class APNGDecoderTest {
	val logger: Logger = ColoredHandler.newLogger("png decoder tests tmp")
	val errorImage = run {
		val error = this::class.java.classLoader.getResourceAsStream("coder/format/png/error.png")!!
		val parsed = PNGParser(BufferedInputStream(error)).toList()
		val imageDecoder = PNGImageDecoder(
			parsed.firstNotNullOf { it.result as? PNGHeaderChunk },
			parsed.firstNotNullOfOrNull { it.result as? PNGPaletteChunk }?.colors ?: listOf(),
			parsed.firstNotNullOfOrNull { it.result as? PNGTransparencyPaletteChunk }?.alphas ?: listOf(),
			parsed.firstNotNullOfOrNull { it.result as? PNGGammaChunk }?.gamma ?: 1.0,
			parsed.firstNotNullOfOrNull { it.result as? PNGBackgroundChunk }?.color ?: Color(0, 0, 0, 0)
		)
		imageDecoder.setInput(parsed.first { it.resultSafe.tag == "IDAT" }.resultSafe.data)
		imageDecoder.next()
	}

	@Test
	fun consumeNext() {
		val testURL = this::class.java.classLoader.getResource("coder/format/png/animated")
		if (testURL == null || testURL.protocol != "file") return logger.severe("Unable to test")
		val grid = JPanel(GridLayout(0, 5, 1, 1))
		grid.background = Color(0, 0, 127)
		fun Path.testDirectory() {
			this.forEachDirectoryEntry { path ->
				if (path.isDirectory()) {
					path.testDirectory()
					return
				}
				logger.info("File $path")
				runCatching {
					val parsed = mutableListOf<CodingPartialResult<PNGChunk>>()
					PNGParser(BufferedInputStream(path.inputStream())).forEach {
						it.resultSafe.dumpLog(logger)
						parsed.add(it)
					}
					val imageDecoder = PNGImageDecoder(
						parsed.firstNotNullOf { it.result as? PNGHeaderChunk },
						parsed.firstNotNullOfOrNull { it.result as? PNGPaletteChunk }?.colors ?: listOf(),
						parsed.firstNotNullOfOrNull { it.result as? PNGTransparencyPaletteChunk }?.alphas ?: listOf(),
						parsed.firstNotNullOfOrNull { it.result as? PNGGammaChunk }?.gamma ?: 1.0,
						parsed.firstNotNullOfOrNull { it.result as? PNGBackgroundChunk }?.color ?: Color(0, 0, 0, 0)
					)
					val apngSequenced = parsed
						.mapNotNull { it.result }
						.mapNotNull { it as? PNGSequencedChunk }
						.sortedBy { it.sequence }
						.toMutableList()
					val fcTlMain = apngSequenced.indexOfFirst { it.sequence == 0 }.let {
						if (it == -1) null
						else apngSequenced.removeAt(it) as PNGFrameControlChunk
					}
					val frames = apngSequenced.chunked(2) { pair ->
						pair[0] as PNGFrameControlChunk to pair[1] as PNGFrameDataChunk
					}
					imageDecoder.setInput(
						parsed.first { it.resultSafe.tag == "IDAT" }.resultSafe.data,
						delay = fcTlMain?.delay ?: Duration.ZERO
					)
					val images = mutableListOf(imageDecoder.next())
					frames.forEach { (control, data) ->
						imageDecoder.setInput(
							data.data,
							control.x,
							control.y,
							control.width,
							control.height,
							control.dispose,
							control.blend,
							control.delay
						)
						val image = imageDecoder.next()
						images.add(image)
					}
					val imagePanel = ImagePanel(images)
					imagePanel.background = Color(127, 0, 0)
					grid.add(imagePanel)
				}.onFailure {
					logger.log(Level.SEVERE, it) { "Error while parsing/decoding image" }
					grid.add(ImagePanel(listOf(errorImage)))
				}
			}
		}
		File(testURL.path)
			.toPath()
			.testDirectory()

		val frame = JFrame("APNG Test").apply {
			defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
			val scroll = JScrollPane(grid)
			scroll.preferredSize = Dimension(500, 500)
			contentPane.add(scroll)
			pack()
			setLocationRelativeTo(null)
			isVisible = true
		}
		while (frame.isDisplayable) Thread.sleep(100)
	}
}