package org.bread_experts_group.protocol.ip.v4

import org.bread_experts_group.Mappable

enum class InternetProtocolV4OptionType(
	override val id: UByte,
	override val tag: String
) : Mappable<InternetProtocolV4OptionType, UByte> {
	END_OF_OPTIONS_LIST(0x00u, "(EOOL) End Of Options List"),
	NO_OPERATION(0x01u, "(NOP) No Operation"),
	ROUTER_ALERT(0x94u, "(RTRALT) Router Alert");

	override fun toString(): String = stringForm()
}