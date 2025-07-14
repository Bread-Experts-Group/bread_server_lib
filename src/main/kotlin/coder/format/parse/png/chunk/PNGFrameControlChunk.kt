package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGBlendOperation
import org.bread_experts_group.coder.format.parse.png.PNGDisposeOperation
import java.nio.channels.SeekableByteChannel
import kotlin.time.Duration

class PNGFrameControlChunk(
	override val sequence: Int,
	val width: Int,
	val height: Int,
	val x: Int,
	val y: Int,
	val delay: Duration,
	val dispose: PNGDisposeOperation,
	val blend: PNGBlendOperation,
	window: SeekableByteChannel
) : PNGChunk("fcTL", window), PNGSequencedChunk {
	override fun toString(): String = super.toString() + "[#$sequence, $width x $height, @$x x $y, " +
			"$delay, $dispose, $blend]"
}