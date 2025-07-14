package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.gif.GIFDisposalMethod
import java.nio.channels.SeekableByteChannel
import kotlin.time.Duration

class PNGGIFGraphicControlExtensionChunk(
	val disposalMethod: GIFDisposalMethod,
	val wantUserInput: Boolean,
	val delayTime: Duration,
	window: SeekableByteChannel
) : PNGChunk("gIFg", window) {
	override fun toString(): String = super.toString() + "[$disposalMethod" +
			"${if (wantUserInput) ", want user input" else ""}, $delayTime]"
}