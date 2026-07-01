package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_X64_SEGMENT_REGISTER : Structure<WHV_X64_SEGMENT_REGISTER> {
	@Order(0)
	abstract var Base: UINT64

	@Order(1)
	abstract var Limit: UINT32

	@Order(2)
	abstract var Selector: UINT16

	@Order(3)
	abstract var Attributes: UINT16

	override fun toString(): String = genericToString(this)
}