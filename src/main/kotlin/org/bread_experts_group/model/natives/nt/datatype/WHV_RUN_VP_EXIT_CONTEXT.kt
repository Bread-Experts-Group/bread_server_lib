package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_RUN_VP_EXIT_CONTEXT : Structure<WHV_RUN_VP_EXIT_CONTEXT> {
	@Order(0)
	abstract var ExitReason: WHV_RUN_VP_EXIT_REASON

	@Order(1)
	abstract var Reserved: UINT32

	@Order(2)
	abstract var VpContext: WHV_VP_EXIT_CONTEXT

	@Order(3)
	abstract var Union: UnionClass

	abstract class UnionClass : Structure<UnionClass> {
		@Order(0)
		abstract var MemoryAccess: WHV_MEMORY_ACCESS_CONTEXT

		@Order(0)
		abstract var IoPortAccess: WHV_X64_IO_PORT_ACCESS_CONTEXT

		@Order(0)
		abstract val VpException: WHV_VP_EXCEPTION_CONTEXT

		override fun toString(): String = genericToString(this)
	}

	override fun toString(): String = genericToString(this)
}