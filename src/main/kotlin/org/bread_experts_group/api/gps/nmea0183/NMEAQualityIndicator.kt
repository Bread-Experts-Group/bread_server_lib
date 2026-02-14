package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.generic.Mappable

enum class NMEAQualityIndicator(
	override val id: Int,
	override val tag: String
) : Mappable<NMEAQualityIndicator, Int> {
	NO_FIX(0, "No Fix / Invalid"),
	STANDARD(1, "Standard GPS (2D/3D)"),
	DIFFERENTIAL(2, "Differential GPS"),
	ESTIMATED(6, "Estimated (DR) Fix");

	override fun toString(): String = stringForm()
}