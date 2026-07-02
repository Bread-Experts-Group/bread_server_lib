package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class _IP_ADAPTER_PREFIX_XP : Structure<_IP_ADAPTER_PREFIX_XP> {
	@Order(0)
	abstract var Union1: U1

	@Order(1)
	abstract var Next: Pointer<_IP_ADAPTER_PREFIX_XP>

	@Order(2)
	abstract var Address: SOCKET_ADDRESS

	@Order(3)
	abstract var PrefixLength: ULONG

	abstract class U1 : Structure<U1> {
		@Order(0)
		abstract var Alignment: ULONGLONG

		@Order(0)
		abstract var Structure: S

		abstract class S : Structure<S> {
			@Order(0)
			abstract var Length: ULONG

			@Order(1)
			abstract var Flags: DWORD
		}
	}
}

typealias IP_ADAPTER_PREFIX_XP = _IP_ADAPTER_PREFIX_XP
typealias PIP_ADAPTER_PREFIX_XP = Pointer<_IP_ADAPTER_PREFIX_XP>