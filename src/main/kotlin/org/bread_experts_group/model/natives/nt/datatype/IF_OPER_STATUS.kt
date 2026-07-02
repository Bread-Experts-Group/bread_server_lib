package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.generic.Mappable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IF_OPER_STATUS : Mappable<IF_OPER_STATUS, Int> {
	IfOperStatusUp,
	IfOperStatusDown,
	IfOperStatusTesting,
	IfOperStatusUnknown,
	IfOperStatusDormant,
	IfOperStatusNotPresent,
	IfOperStatusLowerLayerDown;

	override val id: Int = ordinal + 1
	override val tag: String = name
}