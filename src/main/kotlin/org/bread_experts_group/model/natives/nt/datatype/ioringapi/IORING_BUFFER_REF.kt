@file:Suppress("ClassName")

package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.ntioring_x.IORING_REGISTERED_BUFFER
import java.lang.foreign.MemorySegment

abstract class IORING_BUFFER_REF : Structure<IORING_BUFFER_REF> {
	@Order(0)
	abstract var Kind: IORING_REF_KIND

	@Order(1)
	abstract var Buffer: BufferUnion

	abstract class BufferUnion : Structure<BufferUnion> {
		@Order(0)
		abstract var Address: MemorySegment

		@Order(0)
		abstract var IndexAndOffset: IORING_REGISTERED_BUFFER
	}
}