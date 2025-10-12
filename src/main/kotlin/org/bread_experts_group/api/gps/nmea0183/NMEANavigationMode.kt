package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable

enum class NMEANavigationMode(
	override val id: Int,
	override val tag: String
) : Mappable<NMEANavigationMode, Int> {
	NO_FIX(1, "No Fix Available"),
	FIX_2D(2, "2D Fix"),
	FIX_3D(3, "3D Fix");

	override fun toString(): String = stringForm()
}