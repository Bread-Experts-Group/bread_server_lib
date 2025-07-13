package org.bread_experts_group.coder.format.decode

import java.awt.image.BufferedImage
import kotlin.time.Duration

data class TimedBufferedImage(
	val image: BufferedImage,
	val delay: Duration
)