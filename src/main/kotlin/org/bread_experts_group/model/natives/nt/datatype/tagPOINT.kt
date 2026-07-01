package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class tagPOINT : Structure<tagPOINT> {
	@Order(0)
	abstract var x: LONG

	@Order(1)
	abstract var y: LONG
}

typealias POINT = tagPOINT

typealias PPOINT = Pointer<tagPOINT>
typealias NPPOINT = Pointer<tagPOINT>
typealias LPPOINT = Pointer<tagPOINT>