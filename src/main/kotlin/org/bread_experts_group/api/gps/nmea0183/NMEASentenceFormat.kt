package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.Mappable

enum class NMEASentenceFormat(
	override val tag: String
) : Mappable<NMEASentenceFormat, String> {
	DTM("Datum Reference"),
	GBS("GNSS Satellite Fault Detection"),
	GGA("Global positioning system fix data"),
	GLL("Latitude and longitude, with time of position fix and status"),
	GLQ("Poll a standard message (if the current Talker ID is GL)"),
	GNQ("Poll a standard message (if the current Talker ID is GN)"),
	GNS("GNSS fix data"),
	GPQ("Poll a standard message (if the current Talker ID is GP)"),
	GRS("GNSS Range Residuals"),
	GSA("GNSS DOP and Active Satellites"),
	GST("GNSS Pseudo Range Error Statistics"),
	GSV("GNSS Satellites in View"),
	RMC("Recommended Minimum data"),
	TXT("Text Transmission"),
	VTG("Course over ground and Ground speed"),
	ZDA("Time and Date");

	override val id: String = name
	override fun toString(): String = stringForm()
}