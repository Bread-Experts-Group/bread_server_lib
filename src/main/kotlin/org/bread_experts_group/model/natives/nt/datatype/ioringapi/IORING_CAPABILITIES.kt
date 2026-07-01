package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.IndexedEnumSet
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.UINT32
import org.bread_experts_group.model.natives.nt.datatype.ntioring_x.IORING_FEATURE_FLAGS
import org.bread_experts_group.model.natives.nt.datatype.ntioring_x.IORING_VERSION

abstract class IORING_CAPABILITIES : Structure<IORING_CAPABILITIES> {
	@Order(0)
	abstract var MaxVersion: IORING_VERSION

	@Order(1)
	abstract var MaxSubmissionQueueSize: UINT32

	@Order(2)
	abstract var MaxCompletionQueueSize: UINT32

	@Order(3)
	abstract var FeatureFlags: IndexedEnumSet<IORING_FEATURE_FLAGS>

	override fun toString(): String = genericToString(this)
}