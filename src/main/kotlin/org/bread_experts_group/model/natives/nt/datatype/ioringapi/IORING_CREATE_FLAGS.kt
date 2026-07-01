@file:Suppress("ClassName", "PropertyName")

package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.IndexedEnumSet
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure

abstract class IORING_CREATE_FLAGS : Structure<IORING_CREATE_FLAGS> {
	@Order(0)
	abstract var Required: IndexedEnumSet<IORING_CREATE_REQUIRED_FLAGS>

	@Order(1)
	abstract var Advisory: IndexedEnumSet<IORING_CREATE_ADVISORY_FLAGS>

	override fun toString(): String = genericToString(this)
}