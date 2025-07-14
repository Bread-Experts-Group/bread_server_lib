package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGICPVSTIDColorPrimaries
import org.bread_experts_group.coder.format.parse.png.PNGICPVSTIDMatrixCoefficients
import org.bread_experts_group.coder.format.parse.png.PNGICPVSTIDTransferFunction
import java.nio.channels.SeekableByteChannel

class PNGICPVSTIDChunk(
	val colorPrimaries: PNGICPVSTIDColorPrimaries,
	val transferFunction: PNGICPVSTIDTransferFunction,
	val matrixCoefficients: PNGICPVSTIDMatrixCoefficients,
	val videoFullRange: Boolean,
	window: SeekableByteChannel
) : PNGChunk("cICP", window) {
	override fun toString(): String = super.toString() +
			"[${if (videoFullRange) "Full-Range Image" else "Narrow-Range Image"}, $colorPrimaries, " +
			"$transferFunction, $matrixCoefficients]"
}