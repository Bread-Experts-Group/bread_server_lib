package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class tagMSG : Structure<tagMSG> {
	@Order(0)
	abstract var hwnd: HWND

	@Order(1)
	abstract var message: UINT

	@Order(2)
	abstract var wParam: WPARAM

	@Order(3)
	abstract var lParam: LPARAM

	@Order(4)
	abstract var time: DWORD

	@Order(5)
	abstract var pt: POINT

	@Order(6)
	abstract var lPrivate: DWORD
}

typealias MSG = tagMSG

typealias PMSG = Pointer<tagMSG>
typealias NPMSG = Pointer<tagMSG>
typealias LPMSG = Pointer<tagMSG>