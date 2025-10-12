package org.bread_experts_group.api.gps.nmea0183

data class NMEAGSVSatelliteDetail(
	val id: Int,
	val elevation: Int?,
	val azimuth: Int?,
	val signalStrength: Int?
)