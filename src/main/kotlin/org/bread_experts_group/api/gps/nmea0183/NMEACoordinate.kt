package org.bread_experts_group.api.gps.nmea0183

import java.math.BigDecimal
import java.math.RoundingMode

class NMEACoordinate(
	val degrees: Int,
	val minutes: BigDecimal
) {
	companion object {
		val seconds: BigDecimal = BigDecimal.valueOf(60)
	}

	override fun toString(): String =
		"$degrees°, ${minutes.setScale(0, RoundingMode.DOWN)}', ${minutes.remainder(BigDecimal.ONE) * seconds}\""
}