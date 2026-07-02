package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked
import org.bread_experts_group.model.natives.Pointer

@DatatypeBacked("int")
enum class TUNNEL_TYPE(override val id: Int) : Mappable<TUNNEL_TYPE, Int> {
	TUNNEL_TYPE_NONE(0),
	TUNNEL_TYPE_OTHER(1),
	TUNNEL_TYPE_DIRECT(2),
	TUNNEL_TYPE_6TO4(11),
	TUNNEL_TYPE_ISATAP(13),
	TUNNEL_TYPE_TEREDO(14),
	TUNNEL_TYPE_IPHTTPS(15);

	override val tag: String = name
}

typealias PTUNNEL_TYPE = Pointer<TUNNEL_TYPE>