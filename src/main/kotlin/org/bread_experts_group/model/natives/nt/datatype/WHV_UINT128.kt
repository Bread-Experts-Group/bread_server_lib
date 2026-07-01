package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Align
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

@Align(16)
abstract class WHV_UINT128 : Structure<WHV_UINT128> {
	@Order(0)
	abstract var Low64: UINT64

	@Order(1)
	abstract var High64: UINT64
}