package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class NL_DAD_STATE(override val id: Int) : Mappable<NL_SUFFIX_ORIGIN, Int> {
	IpDadStateInvalid(0),
	IpDadStateTentative(1),
	IpDadStateDuplicate(2),
	IpDadStateDeprecated(3),
	IpDadStatePreferred(4);

	override val tag: String = name
}