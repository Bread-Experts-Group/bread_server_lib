package org.bread_experts_group.model.natives.nt.datatype

import org.bread_experts_group.model.natives.Order
import org.bread_experts_group.model.natives.Pointer
import org.bread_experts_group.model.natives.Structure

abstract class _IP_ADAPTER_UNICAST_ADDRESS_LH : Structure<_IP_ADAPTER_UNICAST_ADDRESS_LH> {
	@Order(0)
	abstract var Union1: U1

	@Order(1)
	abstract var Next: Pointer<_IP_ADAPTER_UNICAST_ADDRESS_LH>

	@Order(2)
	abstract var Address: SOCKET_ADDRESS

	@Order(3)
	abstract var PrefixOrigin: IP_PREFIX_ORIGIN

	@Order(4)
	abstract var SuffixOrigin: IP_SUFFIX_ORIGIN

	@Order(5)
	abstract var DadState: IP_DAD_STATE

	@Order(6)
	abstract var ValidLifetime: ULONG

	@Order(7)
	abstract var PreferredLifetime: ULONG

	@Order(8)
	abstract var LeaseLifetime: ULONG

	@Order(9)
	abstract var OnLinkPrefixLength: UINT8

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

typealias IP_ADAPTER_UNICAST_ADDRESS_LH = _IP_ADAPTER_UNICAST_ADDRESS_LH
typealias PIP_ADAPTER_UNICAST_ADDRESS_LH = Pointer<_IP_ADAPTER_UNICAST_ADDRESS_LH>