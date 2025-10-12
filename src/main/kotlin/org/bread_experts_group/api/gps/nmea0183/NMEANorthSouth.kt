package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable

enum class NMEANorthSouth(
	override val id: Char,
	override val tag: String
) : Mappable<NMEANorthSouth, Char> {
	NORTH('N', "North"),
	SOUTH('S', "South");

	override fun toString(): String = stringForm()
}