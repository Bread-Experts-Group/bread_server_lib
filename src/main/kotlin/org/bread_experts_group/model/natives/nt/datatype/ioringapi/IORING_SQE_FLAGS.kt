@file:Suppress("ClassName")

package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.generic.Flaggable
import org.bread_experts_group.model.natives.DatatypeBacked

@DatatypeBacked("int")
enum class IORING_SQE_FLAGS : Flaggable {
	IOSQE_FLAGS_DRAIN_PRECEDING_OPS;

	override val position: Long = 1L shl ordinal
}