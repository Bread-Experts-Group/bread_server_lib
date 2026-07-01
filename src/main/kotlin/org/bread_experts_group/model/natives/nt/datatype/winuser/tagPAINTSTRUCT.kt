package org.bread_experts_group.model.natives.nt.datatype.winuser

import org.bread_experts_group.model.natives.ArraySize
import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.nt.datatype.BOOL
import org.bread_experts_group.model.natives.nt.datatype.BYTE
import org.bread_experts_group.model.natives.nt.datatype.HDC
import org.bread_experts_group.model.natives.nt.datatype.RECT

abstract class tagPAINTSTRUCT : Structure<tagPAINTSTRUCT> {
	@Order(0)
	abstract var hdc: HDC

	@Order(1)
	abstract var fErase: BOOL

	@Order(2)
	abstract var rcPaint: RECT

	@Order(3)
	abstract var fRestore: BOOL

	@Order(4)
	abstract var fIncUpdate: BOOL

	@Order(5)
	abstract var rgbReserved: @ArraySize(32) BYTE
}

typealias PAINTSTRUCT = tagPAINTSTRUCT

typealias PPAINTSTRUCT = Pointer<tagPAINTSTRUCT>
typealias NPPAINTSTRUCT = Pointer<tagPAINTSTRUCT>
typealias LPPAINTSTRUCT = Pointer<tagPAINTSTRUCT>