@file:Suppress("ClassName")

package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class CREATEFILE2_EXTENDED_PARAMETERS : Structure<CREATEFILE2_EXTENDED_PARAMETERS> {
	@Order(0)
	abstract var dwSize: DWORD

	@Order(1)
	abstract var dwFileAttributes: DWORD

	@Order(2)
	abstract var dwFileFlags: DWORD

	@Order(3)
	abstract var dwSecurityQosFlags: DWORD

	@Order(4)
	abstract var lpSecurityAttributes: LPSECURITY_ATTRIBUTES?

	@Order(5)
	abstract var hTemplateFile: HANDLE
}

typealias PCREATEFILE2_EXTENDED_PARAMETERS = Pointer<CREATEFILE2_EXTENDED_PARAMETERS>
typealias LPCREATEFILE2_EXTENDED_PARAMETERS = Pointer<CREATEFILE2_EXTENDED_PARAMETERS>