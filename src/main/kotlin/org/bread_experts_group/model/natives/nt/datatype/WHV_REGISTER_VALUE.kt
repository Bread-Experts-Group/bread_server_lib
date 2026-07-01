package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_REGISTER_VALUE : Structure<WHV_REGISTER_VALUE> {
	@Order(0)
	abstract var Reg128: WHV_UINT128

	@Order(0)
	abstract var Reg64: UINT64

	@Order(0)
	abstract var Reg32: UINT32

	@Order(0)
	abstract var Reg16: UINT16

	@Order(0)
	abstract var Reg8: UINT8
}