package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class NL_SUFFIX_ORIGIN(override val id: Int) : Mappable<NL_SUFFIX_ORIGIN, Int> {
	IpSuffixOriginOther(0),
	IpSuffixOriginManual(1),
	IpSuffixOriginWellKnown(2),
	IpSuffixOriginDhcp(3),
	IpSuffixOriginLinkLayerAddress(4),
	IpSuffixOriginRandom(5),
	IpSuffixOriginUnchanged(1 shl 4);

	override val tag: String = name
}