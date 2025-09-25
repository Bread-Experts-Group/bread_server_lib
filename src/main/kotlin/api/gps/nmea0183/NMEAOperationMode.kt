package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.coder.Mappable

enum class NMEAOperationMode(
	override val id: Char,
	override val tag: String
) : Mappable<NMEAOperationMode, Char> {
	MANUAL('M', "Manual 2D/3D mode"),
	AUTO('A', "Automatic 2D/3D mode");

	override fun toString(): String = stringForm()
}