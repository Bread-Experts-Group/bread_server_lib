package org.bread_experts_group.model.natives.nt.datatype.com

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.HRESULT
import org.bread_experts_group.model.natives.nt.datatype.REFIID
import java.lang.foreign.MemorySegment

abstract class IUnknownVtbl : Structure<IUnknownVtbl> {
	@Order(0)
	abstract var QueryInterface: (
		self: Pointer<IUnknown>,
		riid: REFIID, ppvObject: Pointer<MemorySegment>
	) -> HRESULT

	@Order(1)
	abstract var AddRef: (
		self: Pointer<IUnknown>
	) -> HRESULT

	@Order(2)
	abstract var Release: (
		self: Pointer<IUnknown>
	) -> HRESULT

	override fun toString(): String = genericToString(this)
}