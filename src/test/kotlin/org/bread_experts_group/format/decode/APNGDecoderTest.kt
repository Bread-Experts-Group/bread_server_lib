package org.bread_experts_group.org.bread_experts_group.format.decode

//class APNGDecoderTest {
//	val logger: Logger = ColoredHandler.newLogger("png decoder tests tmp")
//	val errorImage = run {
//		val error = this::class.java.classLoader.getResource("coder/format/png/error.png")
//			?: throw Error("Missing error image")
//		val parsed = PNGByteParser().setInput(Files.newByteChannel(Paths.get(error.toURI()))).toList()
//		val imageDecoder = PNGImageDecoder(
//			parsed.firstNotNullOf { it.resultSafe as? PNGHeaderChunk },
//			parsed.firstNotNullOfOrNull { it.resultSafe as? PNGPaletteChunk }?.colors ?: listOf(),
//			parsed.firstNotNullOfOrNull { it.resultSafe as? PNGTransparencyPaletteChunk }?.alphas ?: listOf(),
//			parsed.firstNotNullOfOrNull { it.resultSafe as? PNGGammaChunk }?.gamma ?: 1.0,
//			parsed.firstNotNullOfOrNull { it.resultSafe as? PNGBackgroundChunk }?.color ?: Color(0, 0, 0, 0)
//		)
//		imageDecoder.setInput(parsed.firstNotNullOf { it.resultSafe as? PNGDataChunk }.windows)
//		imageDecoder.next()
//	}
//
//	@Test
//	fun consumeNext() {
//		val testURL = this::class.java.classLoader.getResource("coder/format/png/animated")
//		assert(testURL != null && testURL.protocol == "file") { "Unable to test in [$testURL]" }
//		val grid = JPanel(GridLayout(0, 5, 1, 1))
//		grid.background = Color(0, 0, 127)
//		val parser = PNGByteParser()
//		fun Path.testDirectory() {
//			this.forEachDirectoryEntry { path ->
//				if (path.isDirectory()) {
//					path.testDirectory()
//					return
//				}
//				logger.info("File $path")
//				runCatching {
//					val parsed = mutableListOf<CodingPartialResult<PNGChunk>>()
//					parser.setInput(Files.newByteChannel(path)).forEach {
//						it.resultSafe.dumpLog(logger)
//						parsed.add(it)
//					}
//					val imageDecoder = PNGImageDecoder(
//						parsed.firstNotNullOf { it.result as? PNGHeaderChunk },
//						parsed.firstNotNullOfOrNull { it.result as? PNGPaletteChunk }?.colors ?: listOf(),
//						parsed.firstNotNullOfOrNull { it.result as? PNGTransparencyPaletteChunk }?.alphas ?: listOf(),
//						parsed.firstNotNullOfOrNull { it.result as? PNGGammaChunk }?.gamma ?: 1.0,
//						parsed.firstNotNullOfOrNull { it.result as? PNGBackgroundChunk }?.color ?: Color(0, 0, 0, 0)
//					)
//					val apngSequenced = parsed
//						.mapNotNull { it.result }
//						.mapNotNull { it as? PNGSequencedChunk }
//						.sortedBy { it.sequence }
//						.toMutableList()
//					val fcTlMain = apngSequenced.indexOfFirst { it.sequence == 0 }.let {
//						if (it == -1) null
//						else apngSequenced.removeAt(it) as PNGFrameControlChunk
//					}
//					val frames = apngSequenced.chunked(2) { pair ->
//						pair[0] as PNGFrameControlChunk to pair[1] as PNGFrameDataChunk
//					}
//					imageDecoder.setInput(
//						parsed.firstNotNullOf { it.result as? PNGDataChunk }.windows,
//						delay = fcTlMain?.delay ?: Duration.ZERO
//					)
//					val images = mutableListOf(imageDecoder.next())
//					frames.forEach { (control, data) ->
//						imageDecoder.setInput(
//							data.windows,
//							control.x,
//							control.y,
//							control.width,
//							control.height,
//							control.dispose,
//							control.blend,
//							control.delay
//						)
//						try {
//							val image = imageDecoder.next()
//							images.add(image)
//						} catch (_: Exception) {
//						}
//					}
//					val imagePanel = ImagePanel(images)
//					imagePanel.background = Color(127, 0, 0)
//					grid.add(imagePanel)
//				}.onFailure {
//					logger.log(Level.SEVERE, it) { "Error while parsing/decoding image" }
//					grid.add(ImagePanel(listOf(errorImage)))
//				}
//			}
//		}
//		Paths.get(testURL!!.toURI()).testDirectory()
//
//		val frame = JFrame("APNG Test").apply {
//			defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
//			val scroll = JScrollPane(grid)
//			scroll.preferredSize = Dimension(500, 500)
//			contentPane.add(scroll)
//			pack()
//			setLocationRelativeTo(null)
//			isVisible = true
//		}
//		while (frame.isDisplayable) Thread.sleep(100)
//	}
//}