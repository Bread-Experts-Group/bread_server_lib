package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.genericToString
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.com.IUnknown
import java.lang.foreign.MemorySegment

abstract class IGraphicsCaptureItemInteropVtbl : Structure<IGraphicsCaptureItemInteropVtbl> {
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

	@Order(3)
	abstract var CreateForWindow: (
		self: Pointer<IGraphicsCaptureItemInterop>,
		window: HWND,
		riid: REFIID,
		result: Pointer<MemorySegment>
	) -> HRESULT

	@Order(4)
	abstract var CreateForMonitor: (
		self: Pointer<IGraphicsCaptureItemInterop>,
		monitor: HMONITOR,
		riid: REFIID,
		result: Pointer<MemorySegment>
	) -> HRESULT

	override fun toString(): String = genericToString(this)
}