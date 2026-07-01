package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_X64_VP_EXIT_CONTEXT : Structure<WHV_X64_VP_EXIT_CONTEXT> {
	@Order(0)
	abstract var ExecutionState: WHV_X64_VP_EXECUTION_STATE

	@Order(1)
	abstract var InstructionLengthCr8: UINT8

	@Order(2)
	abstract var Reserved: UINT8

	@Order(3)
	abstract var Reserved2: UINT32

	@Order(4)
	abstract var Cs: WHV_X64_SEGMENT_REGISTER

	@Order(5)
	abstract var Rip: UINT64

	@Order(6)
	abstract var Rflags: UINT64

	override fun toString(): String = genericToString(this)
}

typealias WHV_VP_EXIT_CONTEXT = WHV_X64_VP_EXIT_CONTEXT // TODO: ARM64