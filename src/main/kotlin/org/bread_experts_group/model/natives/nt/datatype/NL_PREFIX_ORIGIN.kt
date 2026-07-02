package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class NL_PREFIX_ORIGIN(override val id: Int) : Mappable<NL_SUFFIX_ORIGIN, Int> {
	IpPrefixOriginOther(0),
	IpPrefixOriginManual(1),
	IpPrefixOriginWellKnown(2),
	IpPrefixOriginDhcp(3),
	IpPrefixOriginRouterAdvertisement(4),
	IpPrefixOriginUnchanged(1 shl 4);

	override val tag: String = name
}