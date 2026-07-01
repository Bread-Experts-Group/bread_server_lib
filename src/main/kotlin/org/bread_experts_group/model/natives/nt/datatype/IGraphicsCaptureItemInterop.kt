package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class IGraphicsCaptureItemInterop : Structure<IGraphicsCaptureItemInterop> {
	@Order(0)
	abstract var lpVtbl: Pointer<IGraphicsCaptureItemInteropVtbl>

	override fun toString(): String = genericToString(this)
}