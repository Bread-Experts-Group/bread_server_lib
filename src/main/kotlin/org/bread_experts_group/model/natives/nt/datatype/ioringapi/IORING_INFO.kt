@file:Suppress("PropertyName", "ClassName")

package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.UINT32
import org.bread_experts_group.model.natives.nt.datatype.ntioring_x.IORING_VERSION

abstract class IORING_INFO : Structure<IORING_INFO> {
	@Order(0)
	abstract var IoRingVersion: IORING_VERSION

	@Order(1)
	abstract var Flags: IORING_CREATE_FLAGS

	@Order(2)
	abstract var SubmissionQueueSize: UINT32

	@Order(3)
	abstract var CompletionQueueSize: UINT32

	override fun toString(): String = genericToString(this)
}