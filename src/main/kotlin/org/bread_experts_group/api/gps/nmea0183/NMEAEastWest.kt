package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable

enum class NMEAEastWest(
	override val id: Char,
	override val tag: String
) : Mappable<NMEAEastWest, Char> {
	EAST('E', "East"),
	WEST('W', "West");

	override fun toString(): String = stringForm()
}