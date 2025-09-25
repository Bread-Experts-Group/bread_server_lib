package org.bread_experts_group.protocol.ntp

import org.bread_experts_group.coder.Mappable

enum class NTPAssociationMode(
	override val id: Int,
	override val tag: String
) : Mappable<NTPAssociationMode, Int> {
	SYMMETRIC_ACTIVE(1, "Symmetric Active"),
	SYMMETRIC_PASSIVE(2, "Symmetric Passive"),
	CLIENT(3, "Client"),
	SERVER(4, "Server"),
	BROADCAST(5, "Broadcast"),
	CONTROL(6, "Control"),
	PRIVATE(7, "Private");

	override fun toString(): String = stringForm()
}