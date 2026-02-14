package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.generic.Mappable

enum class NMEATalker(
	override val id: String, override val tag: String
) : Mappable<NMEATalker, String> {
	GPS_RECEIVER("GP", "GPS receiver");

	override fun toString(): String = stringForm()
}