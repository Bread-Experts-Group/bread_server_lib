package org.bread_experts_group.model.natives.nt.datatype.com

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

// defined in um/Unknwn.h [Windows Kit (10) 10.0.26100.0 Include]
abstract class IUnknown : Structure<IUnknown> {
	@Order(0)
	abstract var lpVtbl: Pointer<IUnknownVtbl>

	override fun toString(): String = genericToString(this)
}