package org.bread_experts_group.model.natives.nt.datatype.winuser

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure
import org.bread_experts_group.model.natives.c.int_t
import org.bread_experts_group.model.natives.nt.datatype.*

abstract class tagWNDCLASSEXA : Structure<tagWNDCLASSEXA> {
	@Order(0)
	abstract var cbSize: UINT

	@Order(1)
	abstract var style: UINT

	@Order(2)
	abstract var lpfnWndProc: WNDPROC

	@Order(3)
	abstract var cbClsExtra: int_t

	@Order(4)
	abstract var cbWndExtra: int_t

	@Order(5)
	abstract var hInstance: HINSTANCE

	@Order(6)
	abstract var hIcon: HICON

	@Order(7)
	abstract var hCursor: HCURSOR

	@Order(8)
	abstract var hbrBackground: HBRUSH

	@Order(9)
	abstract var lpszMenuName: LPCSTR

	@Order(10)
	abstract var lpszClassName: LPCSTR

	@Order(11)
	abstract var hIconSm: HICON
}

typealias WNDCLASSEXA = tagWNDCLASSEXA

typealias PWNDCLASSEXA = Pointer<tagWNDCLASSEXA>
typealias NPWNDCLASSEXA = Pointer<tagWNDCLASSEXA>
typealias LPWNDCLASSEXA = Pointer<tagWNDCLASSEXA>