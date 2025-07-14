package org.bread_experts_group.coder.format.parse.png.chunk

import org.bread_experts_group.coder.format.parse.png.PNGPixelCalibrationEquationType
import java.math.BigDecimal
import java.nio.channels.SeekableByteChannel

class PNGPixelCalibrationChunk(
	val calibrationName: String,
	val originalZero: Int,
	val originalMax: Int,
	val equationType: PNGPixelCalibrationEquationType,
	val unitName: String,
	val parameters: List<BigDecimal>,
	window: SeekableByteChannel
) : PNGChunk("pCAL", window) {
	override fun toString(): String = super.toString() + "[\"$calibrationName\": $originalZero .. $originalMax, " +
			"${
				String.format(
					equationType.toString(),
					originalZero, originalMax, *parameters.toTypedArray()
				)
			}, \"$unitName\": $parameters]"
}