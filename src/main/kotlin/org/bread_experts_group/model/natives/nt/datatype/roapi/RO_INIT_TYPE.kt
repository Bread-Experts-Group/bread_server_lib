package org.bread_experts_group.model.natives.nt.datatype.roapi

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class RO_INIT_TYPE : Mappable<RO_INIT_TYPE, Int> {
	RO_INIT_SINGLETHREADED,
	RO_INIT_MULTITHREADED;

	override val id: Int = ordinal
	override val tag: String = name
}

