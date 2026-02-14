package org.bread_experts_group.api.gps.nmea0183

import org.bread_experts_group.generic.Mappable

enum class NMEAReceiverStatus(
	override val id: Char,
	override val tag: String
) : Mappable<NMEAReceiverStatus, Char> {
	RECEIVER_WARNING('V', "Data invalid or receiver warning"),
	DATA_VALID('A', "Data valid");

	override fun toString(): String = stringForm()
}