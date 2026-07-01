package org.bread_experts_group.model.natives.nt.datatype.projectedfslib

import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.NativeArray
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.UINT8

abstract class PRJ_PLACEHOLDER_VERSION_INFO : Structure<PRJ_PLACEHOLDER_VERSION_INFO> {
	@Order(0)
	abstract var ProviderID: @ArraySize(PRJ_PLACEHOLDER_ID_LENGTH.toLong()) NativeArray<UINT8>

	@Order(1)
	abstract var ContentID: @ArraySize(PRJ_PLACEHOLDER_ID_LENGTH.toLong()) NativeArray<UINT8>
}