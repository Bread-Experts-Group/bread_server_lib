package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class SECURITY_ATTRIBUTES : Structure<SECURITY_ATTRIBUTES> {
	@Order(0)
	abstract var nLength: DWORD

	@Order(1)
	abstract var lpSecurityDescriptor: LPVOID

	@Order(2)
	abstract var bInheritHandle: BOOL
}

typealias PSECURITY_ATTRIBUTES = Pointer<SECURITY_ATTRIBUTES>
typealias LPSECURITY_ATTRIBUTES = Pointer<SECURITY_ATTRIBUTES>