package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IORING_REF_KIND(override val id: Int) : Mappable<IORING_REF_KIND, Int> {
	IORING_REF_RAW(0),
	IORING_REF_REGISTERED(1);

	override val tag: String = name
}