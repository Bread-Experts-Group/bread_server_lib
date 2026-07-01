package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_VP_EXCEPTION_CONTEXT : Structure<WHV_VP_EXCEPTION_CONTEXT> {
	@Order(0)
	abstract var InstructionByteCount: UINT8

	@Order(1)
	abstract var Reserved: @ArraySize(3) NativeArray<UINT8>

	@Order(2)
	abstract var InstructionBytes: @ArraySize(16) NativeArray<UINT8>

	@Order(3)
	abstract var ExceptionInfo: WHV_VP_EXCEPTION_INFO

	@Order(4)
	abstract var ExceptionType: UINT8

	@Order(5)
	abstract var Reserved2: @ArraySize(3) NativeArray<UINT8>

	@Order(6)
	abstract var ErrorCode: UINT32

	@Order(7)
	abstract var ExceptionParameter: UINT64

	override fun toString(): String = genericToString(this)
}