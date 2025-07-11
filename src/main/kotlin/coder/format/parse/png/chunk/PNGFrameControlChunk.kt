package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGBlendOperation
import org.bread_experts_group.coder.format.parse.png.PNGDisposeOperation
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class PNGFrameControlChunk(
	override val sequence: Int,
	val width: Int,
	val height: Int,
	val x: Int,
	val y: Int,
	val delayMillis: Long,
	val dispose: PNGDisposeOperation,
	val blend: PNGBlendOperation
) : PNGChunk("fcTL", byteArrayOf()), PNGSequencedChunk {
	override fun toString(): String = super.toString() + "[#$sequence, $width x $height, @$x x $y, " +
			"${delayMillis.toDuration(DurationUnit.MILLISECONDS)}, $dispose, $blend]"
}