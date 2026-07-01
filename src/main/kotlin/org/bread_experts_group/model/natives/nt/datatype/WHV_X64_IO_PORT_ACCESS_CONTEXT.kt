package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_X64_IO_PORT_ACCESS_CONTEXT : Structure<WHV_X64_IO_PORT_ACCESS_CONTEXT> {
	@Order(0)
	abstract var InstructionByteCount: UINT8

	@Order(1)
	abstract var Reserved: @ArraySize(3) NativeArray<UINT8>

	@Order(2)
	abstract var InstructionBytes: @ArraySize(16) NativeArray<UINT8>

	@Order(3)
	abstract var AccessInfo: WHV_X64_IO_PORT_ACCESS_INFO

	@Order(4)
	abstract var PortNumber: UINT16

	@Order(5)
	abstract var Reserved2: @ArraySize(3) NativeArray<UINT8>

	@Order(6)
	abstract var Rax: UINT64

	@Order(7)
	abstract var Rcx: UINT64

	@Order(8)
	abstract var Rsi: UINT64

	@Order(9)
	abstract var Rdi: UINT64

	@Order(10)
	abstract var Ds: WHV_X64_SEGMENT_REGISTER

	@Order(11)
	abstract var Es: WHV_X64_SEGMENT_REGISTER

	override fun toString(): String = genericToString(this)
}