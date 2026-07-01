package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IORING_CREATE_ADVISORY_FLAGS : Flaggable {
	IORING_CREATE_SKIP_BUILDER_PARAM_CHECKS;

	override val position: Long = 1L shl ordinal
}