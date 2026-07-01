package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

// TODO: ARM64
abstract class WHV_MEMORY_ACCESS_CONTEXT : Structure<WHV_MEMORY_ACCESS_CONTEXT> {
	@Order(0)
	abstract var InstructionByteCount: UINT8

	@Order(1)
	abstract var Reserved: @ArraySize(3) NativeArray<UINT8>

	@Order(2)
	abstract var InstructionBytes: @ArraySize(16) NativeArray<UINT8>

	@Order(3)
	abstract var AccessInfo: WHV_MEMORY_ACCESS_INFO

	@Order(4)
	abstract var Gpa: WHV_GUEST_PHYSICAL_ADDRESS

	@Order(5)
	abstract var Gva: WHV_GUEST_VIRTUAL_ADDRESS

	override fun toString(): String = genericToString(this)
}