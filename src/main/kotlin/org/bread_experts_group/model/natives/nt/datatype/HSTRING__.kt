package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.c.int_t

abstract class HSTRING__ : Structure<HSTRING__> {
	@Order(0)
	abstract var unused: int_t
}