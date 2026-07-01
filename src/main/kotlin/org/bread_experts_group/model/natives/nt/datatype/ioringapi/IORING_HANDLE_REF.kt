@file:Suppress("PropertyName")

package org.bread_experts_group.model.natives.nt.datatype.ioringapi

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.HANDLE
import org.bread_experts_group.model.natives.nt.datatype.UINT32

abstract class IORING_HANDLE_REF : Structure<IORING_HANDLE_REF> {
	@Order(0)
	abstract var Kind: IORING_REF_KIND

	@Order(1)
	abstract var Handle: HandleUnion

	abstract class HandleUnion : Structure<HandleUnion> {
		@Order(0)
		abstract var Handle: HANDLE

		@Order(0)
		abstract var Index: UINT32

		override fun toString(): String = genericToString(this)
	}

	override fun toString(): String = genericToString(this)
}