package org.bread_experts_group.protocol.ntp

import org.bread_experts_group.Mappable

enum class NTPLeapIndicator(
	override val id: Int,
	override val tag: String
) : Mappable<NTPLeapIndicator, Int> {
	NONE(0, "No Leap Warning"),
	FORWARD(1, "Last minute has 61 seconds"),
	BACKWARD(2, "Last minute has 59 seconds"),
	UNKNOWN(3, "Not Synchronized");

	override fun toString(): String = stringForm()
}