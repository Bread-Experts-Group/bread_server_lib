package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable

enum class NMEAPositioningMode(
	override val id: Char,
	override val tag: String
) : Mappable<NMEAPositioningMode, Char> {
	NO_FIX('N', "No Fix"),
	ESTIMATED('E', "Estimated / Dead Reckoning Fix"),
	AUTO('A', "Autonomous GNSS Fix"),
	DIFFERENTIAL('D', "Differential GNSS Fix");

	override fun toString(): String = stringForm()
}