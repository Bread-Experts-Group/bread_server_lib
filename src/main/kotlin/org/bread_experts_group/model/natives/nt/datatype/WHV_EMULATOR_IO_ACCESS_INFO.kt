package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class WHV_EMULATOR_IO_ACCESS_INFO : Structure<WHV_EMULATOR_IO_ACCESS_INFO> {
	@Order(0)
	abstract var Direction: UINT8

	@Order(1)
	abstract var Port: UINT16

	@Order(2)
	abstract var AccessSize: UINT16

	@Order(3)
	abstract var Data: @ArraySize(4) NativeArray<UINT32>

	override fun toString(): String = genericToString(this)
}