package org.bread_experts_group.model.natives.nt.datatype.ntioring_x

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IORING_FEATURE_FLAGS : Flaggable {
	IORING_FEATURE_UM_EMULATION,
	IORING_FEATURE_SET_COMPLETION_EVENT;

	override val position: Long = 1L shl ordinal
}