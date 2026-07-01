package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.HRESULT
import org.bread_experts_group.model.natives.nt.datatype.UINT_PTR
import org.bread_experts_group.model.natives.nt.datatype.ULONG_PTR

abstract class IORING_CQE : Structure<IORING_CQE> {
	@Order(0)
	abstract var UserData: UINT_PTR

	@Order(1)
	abstract var ResultCode: HRESULT

	@Order(2)
	abstract var Information: ULONG_PTR

	override fun toString(): String = genericToString(this)
}