package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class tagRECT : Structure<tagRECT> {
	@Order(0)
	abstract var left: LONG

	@Order(1)
	abstract var top: LONG

	@Order(2)
	abstract var right: LONG

	@Order(3)
	abstract var bottom: LONG
}

typealias RECT = tagRECT

typealias PRECT = Pointer<tagRECT>
typealias NPRECT = Pointer<tagRECT>
typealias LPRECT = Pointer<tagRECT>