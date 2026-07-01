package org.bread_experts_group.model.natives.nt.datatype.ntioring_x

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.UINT32

abstract class IORING_REGISTERED_BUFFER : Structure<IORING_REGISTERED_BUFFER> {
	@Order(0)
	abstract var BufferIndex: UINT32

	@Order(1)
	abstract var Offset: UINT32
}