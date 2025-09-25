package org.bread_experts_group.api.gps.nmea0183

interface NMEAReader {
	fun nextNMEAMessage(): NMEAMessage
}